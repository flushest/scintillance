package com.scintillance.common.exception;

/**
 * Created by Administrator on 2018/2/27 0027.
 */
public class SciException extends RuntimeException {
    public SciException() {
        super();
    }

    public SciException(String msg) {
        super(msg);
    }

    public SciException(String msg,Throwable cause) {
        super(msg,cause);
    }

    public SciException(Throwable cause) {
        super(cause);
    }
}
