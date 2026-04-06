package com.chaykin.common.exception;

public class ExceptionMessageModel {

    private String message;

    public ExceptionMessageModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
