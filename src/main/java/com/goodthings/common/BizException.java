package com.goodthings.common;

public class BizException extends RuntimeException {
    private int code = 400;

    public BizException(String message) {
        super(message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
