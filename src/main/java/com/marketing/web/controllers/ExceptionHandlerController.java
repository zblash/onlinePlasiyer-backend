package com.marketing.web.controllers;

import com.marketing.web.errors.BadRequestException;
import com.marketing.web.errors.HttpMessage;
import com.marketing.web.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.Objects;

@RestControllerAdvice
public class ExceptionHandlerController {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request, Locale locale){
        HttpMessage error = new HttpMessage(HttpStatus.NOT_FOUND);
        error.setMessage(messageSource.getMessage(ex.getMessage(),new Object[] {ex.getId()}, locale));
        return buildResponseEntity(error, (ServletWebRequest) request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(RuntimeException ex, WebRequest request){
        HttpMessage error = new HttpMessage(HttpStatus.BAD_REQUEST);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error, (ServletWebRequest) request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        HttpMessage error = new HttpMessage(HttpStatus.BAD_REQUEST);
        error.setMessage("Required request body is missing");
        return buildResponseEntity(error, (ServletWebRequest) request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        HttpMessage error = new HttpMessage(HttpStatus.BAD_REQUEST);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error, (ServletWebRequest) request);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<?> handlePropertyReferenceException(PropertyReferenceException ex, WebRequest request) {
        HttpMessage error = new HttpMessage(HttpStatus.BAD_REQUEST);
        error.setMessage(ex.getMessage());
        return buildResponseEntity(error, (ServletWebRequest) request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodNotValidException(MethodArgumentNotValidException ex, WebRequest request){
        HttpMessage error = new HttpMessage(HttpStatus.BAD_REQUEST);
        error.setMessage("Validation error");
        error.addValidationErrors(ex.getBindingResult().getFieldErrors());
        error.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(error, (ServletWebRequest) request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request){
        HttpMessage error = new HttpMessage(HttpStatus.BAD_REQUEST);
        error.setMessage(Objects.requireNonNull(ex.getRootCause()).getMessage());
        return buildResponseEntity(error, (ServletWebRequest) request);
    }

    private ResponseEntity<?> buildResponseEntity(HttpMessage error, ServletWebRequest request){
        error.setPath(request.getRequest().getRequestURL().toString());
        return new ResponseEntity<>(error, error.getStatus());
    }
}
