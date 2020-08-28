package com.webank.bcreconcile.exception;

/**
 * @author wesleywang
 * @Description:
 * @date 2020/6/16
 */
public class ReconcileException extends Exception{

    private static final long serialVersionUID = 1L;

    public ReconcileException(){}

    public ReconcileException(String message, Throwable cause){
        super(message,cause);
    }

    public ReconcileException(String message){
        super(message);
    }

}
