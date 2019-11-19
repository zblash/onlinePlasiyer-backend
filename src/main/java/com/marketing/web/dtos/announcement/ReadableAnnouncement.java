package com.marketing.web.dtos.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableAnnouncement implements Serializable {

    private String id;

    private String title;

    private String message;

    private String fileUrl;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date lastDate;

}
