package com.netstudy.media.service;

import com.netstudy.media.model.po.MediaProcess;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description 任务处理
 * @date 2024/5/6 15:12
 **/
public interface MediaFileProcessService {
    /**
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count      获取记录数
     * @return java.util.List<com.netstudy.media.model.po.MediaProcess>
     * @description 获取待处理任务
     * @author Dico
     * @date 2024/5/6 14:49
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 开启一个任务
     *
     * @param id 任务id
     * @return true开启任务成功，false开启任务失败
     */
    public boolean startTask(long id);


    /**
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     * @description 保存任务结果
     * @author Dico
     * @date 2024/5/6 11:29
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);
}
