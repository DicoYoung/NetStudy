package com.netstudy.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Dico
 * @version 1.0
 * @description 熔断降级返回采用的方法
 * @date 2024/5/15 17:05
 **/
public class MediaServiceClientFallback implements MediaServiceClient {
    @Override
    public String upload(MultipartFile filedata, String objectName) throws IOException {

        return null;
    }
}
