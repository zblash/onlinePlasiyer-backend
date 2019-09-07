package com.marketing.web.dtos.ticket;

import com.marketing.web.dtos.DTO;
import com.marketing.web.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableTicket extends DTO {

    private String id;

    private String title;

    private TicketStatus status;

    private String openerName;

    private Date addedTime;
}