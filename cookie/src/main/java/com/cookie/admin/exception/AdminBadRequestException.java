package com.cookie.admin.exception;

public class AdminBadRequestException extends RuntimeException{

    public  AdminBadRequestException(String message) {
        super(message);
    }
}
