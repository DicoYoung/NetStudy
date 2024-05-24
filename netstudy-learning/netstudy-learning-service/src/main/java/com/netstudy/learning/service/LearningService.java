package com.netstudy.learning.service;

import com.netstudy.base.model.RestResponse;

/**
 * @author Dico
 * @version 1.0
 * @description 学习service
 * @date 2024/5/24 15:56
 **/
public interface LearningService {
    RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
