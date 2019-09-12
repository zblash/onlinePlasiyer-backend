package com.marketing.web.validations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImgValidator implements ConstraintValidator<ValidImg, MultipartFile> {

    private Logger logger = LoggerFactory.getLogger(ImgValidator.class);

    @Override
    public void initialize(ValidImg constraintAnnotation) {

    }


    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (isSupportedContentType(multipartFile.getContentType())) return true;
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "Invalid file type.You can only upload the img type files.")
                .addConstraintViolation();
        return false;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}