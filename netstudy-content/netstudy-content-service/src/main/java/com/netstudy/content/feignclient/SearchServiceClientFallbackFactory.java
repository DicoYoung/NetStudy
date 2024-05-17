package com.netstudy.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Dico
 * @version 1.0
 * @description 搜索熔断降级
 * @date 2024/5/15 17:06
 **/
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.error("添加课程索引发生熔断,索引信息:{},熔断异常:{}", courseIndex, throwable.toString(), throwable);
                //走降级了返回 false
                return false;
            }
        };
    }
}
