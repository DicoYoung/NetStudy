package com.netstudy.media.service.jobhandler;

import com.netstudy.base.utils.Mp4VideoUtil;
import com.netstudy.media.model.po.MediaProcess;
import com.netstudy.media.service.MediaFileProcessService;
import com.netstudy.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51 Dico 2024/5/6
 */
@Slf4j
@Component
public class VideoTask {
    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;

    //ffmpeg的路径
    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数

        //确定cpu的核心数
        int processors = Runtime.getRuntime().availableProcessors();
        //查询待处理的任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);

        //任务数量
        int size = mediaProcessList.size();
        log.debug("取到视频处理任务数:" + size);
        if (size <= 0) {
            log.debug("查询到的待处理任务数为0");
            return;
        }
        //创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //使用的计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            //将任务加入线程池
            executorService.execute(() -> {
                try {
                    String status = mediaProcess.getStatus();
                    if ("2".equals(status)) {
                        log.debug("该视频已经被处理，无需再次处理。视频信息：{}", mediaProcess);
                        return;
                    }
                    //任务id
                    Long taskId = mediaProcess.getId();
                    //文件id就是md5
                    String fileId = mediaProcess.getFileId();
                    //开启任务
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败,任务id:{}", taskId);
                        return;
                    }

                    //桶
                    String bucket = mediaProcess.getBucket();
                    //objectName
                    String objectName = mediaProcess.getFilePath();

                    //下载minio视频到本地
                    File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                    if (file == null) {
                        log.debug("下载视频出错,任务id:{},bucket:{},objectName:{}", taskId, bucket, objectName);
                        //保存任务处理失败的结果
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载视频到本地失败");
                        return;
                    }

                    //源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    //转换后mp4文件的路径
                    //先创建一个临时文件，作为转换后的文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常,{}", e.getMessage());
                        //保存任务处理失败的结果
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件异常");
                        return;
                    }
                    String mp4_path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, video_path, mp4_name, mp4_path);
                    //开始视频转换，成功将返回success,失败返回失败原因
                    String result = videoUtil.generateMp4();
                    if (!result.equals("success")) {

                        log.debug("视频转码失败,原因:{},bucket:{},objectName:{},", result, bucket, objectName);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, result);
                        return;

                    }
                    //上传到minio
                    // 根据文件md5，生成objectName
                    boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, getFilePath(fileId, ".mp4"));
                    if (!b1) {
                        log.debug("上传mp4到minio失败,taskid:{}", taskId);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "上传mp4到minio失败");
                        return;
                    }
                    //mp4文件的url，切记要加上桶的名字，因为getFilePath只有文件夹和文件名，没有桶名
                    String url = "/" + bucket + "/" + getFilePath(fileId, ".mp4");

                    //更新任务状态为成功
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "2", fileId, url, "创建临时文件异常");
                } finally {
                    //计算器减去1
                    countDownLatch.countDown();
                }
            });
        });

        //阻塞,指定最大限制的等待时间，阻塞最多等待一定的时间后就解除阻塞
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    private String getFilePath(String fileMd5, String fileExt) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }
}