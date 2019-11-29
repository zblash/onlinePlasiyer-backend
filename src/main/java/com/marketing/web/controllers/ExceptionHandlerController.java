package com.marketing.web.controllers;

import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.HttpMessage;
import com.marketing.web.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Date;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(RuntimeException ex, WebRequest request){

        HttpMessage httpMessage = new HttpMessage(new Date(),HttpStatus.NOT_FOUND.value(),"Not Found",ex.getMessage(),((ServletWebRequest)request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(httpMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(RuntimeException ex, WebRequest request){

        HttpMessage httpMessage = new HttpMessage(new Date(),HttpStatus.BAD_REQUEST.value(),"Bad Request",ex.getMessage(),((ServletWebRequest)request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(httpMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        HttpMessage httpMessage = new HttpMessage(new Date(),HttpStatus.BAD_REQUEST.value(),"Bad Request","Required request body is missing",((ServletWebRequest)request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(httpMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        HttpMessage httpMessage = new HttpMessage(new Date(),HttpStatus.BAD_REQUEST.value(),"Bad Request",ex.getMessage(),((ServletWebRequest)request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(httpMessage, HttpStatus.BAD_REQUEST);
    }
}
