package com.marketing.web.dtos.ticket;

import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableTicketReply extends DTO {

    private String id;

    private String message;

    private String username;

    private Date addedTime;
}
