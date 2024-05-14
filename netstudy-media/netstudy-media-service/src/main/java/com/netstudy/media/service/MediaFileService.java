package com.netstudy.media.service;

import com.netstudy.base.model.PageParams;
import com.netstudy.base.model.PageResult;
import com.netstudy.base.model.RestResponse;
import com.netstudy.media.model.dto.QueryMediaParamsDto;
import com.netstudy.media.model.dto.UploadFileParamsDto;
import com.netstudy.media.model.dto.UploadFileResultDto;
import com.netstudy.media.model.po.MediaFiles;

import java.io.File;

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

    /**
     * @param fileMd5 文件的md5
     * @return com.netstudy.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @description 检查文件是否存在
     * @author Dico
     * @date 2024/4/28 15:38
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @param fileMd5    文件的md5
     * @param chunkIndex 分块序号
     * @return com.netstudy.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @description 检查分块是否存在
     * @author Dico
     * @date 2024/4/28 15:39
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * @param fileMd5            文件md5
     * @param chunk              分块序号
     * @param localChunkFilePath 分块文件本地路径
     * @return com.netstudy.base.model.RestResponse
     * @description 上传分块
     * @author Dico
     * @date 2024/4/28 15:50
     */
    RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath);

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.netstudy.base.model.RestResponse
     * @description 合并分块
     * @author Dico
     * @date 2024/4/28 15:56
     */
    RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 从minio下载文件
     *
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    File downloadFileFromMinIO(String bucket, String objectName);

    /**
     * 将文件添加上传到Minio
     *
     * @param localFilePath 本地文件路径
     * @param mimeType      文件mimetype
     * @param bucket        桶的名称
     * @param objectName    对象名称
     * @return 是否成功
     */
    boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);

    /**
     * 根据媒资主键查询媒资
     *
     * @param mediaId 媒资ID主键
     * @return 媒资信息
     */
    MediaFiles getFileById(String mediaId);
}
