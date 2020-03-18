package com.marketing.web.dtos.ticket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableTicketReply implements Serializable {

    private String id;

    private String message;

    private String username;

    @JsonFormat(pattern="dd-MM-yyyy")
    private Date addedTime;
}
