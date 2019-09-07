package com.marketing.web.controllers;

import com.marketing.web.errors.ErrorMessage;
import com.marketing.web.errors.ResourceNotFoundException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Locale;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(RuntimeException ex, WebRequest request){

        ErrorMessage errorMessage = new ErrorMessage(new Date(),HttpStatus.NOT_FOUND.value(),"Not Found",ex.getMessage(),((ServletWebRequest)request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(new Date(),HttpStatus.BAD_REQUEST.value(),"Bad Request","Required request body is missing",((ServletWebRequest)request).getRequest().getRequestURL().toString());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
