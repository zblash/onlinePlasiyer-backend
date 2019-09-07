package com.marketing.web.errors;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message){
        super(message);
    }

}