package com.marketing.web.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpMessage {


    private Date timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

}
