package com.netstudy.base.exception;

import java.io.Serializable;

/**
 * @author Dico
 * @version 1.0
 * @description 和前端约定返回的异常信息模型
 * @date 2024/4/14 11:19
 **/
public class RestErrorResponse implements Serializable {
    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
