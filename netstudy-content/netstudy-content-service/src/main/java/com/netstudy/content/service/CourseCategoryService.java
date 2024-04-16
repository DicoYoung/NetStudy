package com.netstudy.content.service;

import com.netstudy.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author Dico
 * @version 1.0
 * @description TODO
 * @date 2024/4/12 14:31
 **/
public interface CourseCategoryService {
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
