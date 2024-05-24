package com.netstudy.learning.feignclient;

import com.netstudy.base.model.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Dico
 * @version 1.0
 * @description TODO
 * @date 2024/5/24 9:42
 */
@FeignClient(value = "media-api", fallbackFactory = MediaServiceClientFallbackFactory.class)
@RequestMapping("/media")
public interface MediaServiceClient {

    /**
     * 获取媒资url
     *
     * @param mediaId 媒资id
     * @return 结果
     */
    @GetMapping("/preview/{mediaId}")
    RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId);

}
