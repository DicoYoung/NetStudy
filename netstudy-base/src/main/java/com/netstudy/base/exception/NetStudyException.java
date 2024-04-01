package com.netstudy.base.exception;

/**
 * @author Dico
 * @version 1.0
 * @description 自定义异常类
 * @date 2024/4/14 11:22
 **/
public class NetStudyException extends RuntimeException {
    private String errMessage;

    public NetStudyException() {

    }

    public NetStudyException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public static void cast(String message) {
        throw new NetStudyException(message);
    }

    public static void cast(CommonError error) {
        throw new NetStudyException(error.getErrMessage());
    }
}
