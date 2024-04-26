package com.netstudy.media.service;

import com.netstudy.base.model.PageParams;
import com.netstudy.base.model.PageResult;
import com.netstudy.media.model.dto.QueryMediaParamsDto;
import com.netstudy.media.model.dto.UploadFileParamsDto;
import com.netstudy.media.model.dto.UploadFileResultDto;
import com.netstudy.media.model.po.MediaFiles;

/**
 * @author Dico
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2024/4/22 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.netstudy.base.model.PageResult<po.com.netstudy.media.model.MediaFiles>
     * @description 媒资文件查询方法
     * @author Dico
     * @date 2024/4/22 8:57
     */
    PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     *
     * @param companyId           机构ID
     * @param uploadFileParamsDto 文件信息
     * @param localFilePath       文件路径
     * @return 上传文件结果
     */
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName);

    /**
     * 文件上传到数据库
     *
     * @param companyId           机构ID
     * @param fileMd5             md5信息
     * @param uploadFileParamsDto 上传文件参数
     * @param bucket              桶妹子文件
     * @param objectName          文件名
     * @return 媒资文件
     */
    MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);
}
