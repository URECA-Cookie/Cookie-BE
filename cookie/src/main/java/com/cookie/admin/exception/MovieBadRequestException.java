package com.cookie.admin.exception;

public class MovieBadRequestException extends RuntimeException{

    public MovieBadRequestException(String message) {
        super(message);
    }
}
