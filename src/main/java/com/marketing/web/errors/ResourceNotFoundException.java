package com.marketing.web.errors;


public class ResourceNotFoundException extends RuntimeException {

    private String id;

    public ResourceNotFoundException(String message, String id){
        super(message);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
