package com.netstudy.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;

/**
 * @author Dico
 * @version 1.0
 * @description Minio测试SDK
 * @date 2024/4/23 19:52
 **/
public class MinioTest {

    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    //上传测试
    @Test
    public void test_upload() throws Exception {
        //通过扩展名得到媒体资源文件类型
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".jpg");
        String mineType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通过mineType，字节流
        if (extensionMatch != null) {
            mineType = extensionMatch.getMimeType();
        }

        //上传文件参数设置
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("mediafiles")//桶
                .filename("C:\\Users\\Dico\\Desktop\\keti.jpg")//本地文件
//                .object("keti.jpg")//桶根目录下里的文件名
                .object("test/keti.jpg")//子目录
                .contentType(mineType)//文件扩展名类型
                .build();
        //上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }

    //删除测试
    @Test
    public void test_delete() throws Exception {
        //构建删除文件参数对象
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("mediafiles")
                .object("keti.jpg")
                .build();
        //删除
        minioClient.removeObject(removeObjectArgs);
    }

    //查询测试 从minio下载
    @Test
    public void test_find() throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("mediafiles")
                .object("test/keti.jpg")
                .build();
        //远程服务器流
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        //指定输出流
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Dico\\Desktop\\keti11.jpg");

        IOUtils.copy(inputStream, outputStream);
        //根据md5校验文件是否下载完整,远程流下载下来其实是不一样的，只能和本地源文件比较一下试试
        String source_file = DigestUtils.md5Hex(inputStream);
        String local_file = DigestUtils.md5Hex("C:\\Users\\Dico\\Desktop\\keti11.jpg");
        if (source_file.equals(local_file)) {
            System.out.println("download success");
        } else {
            System.out.println("download failed");
        }
    }
}
