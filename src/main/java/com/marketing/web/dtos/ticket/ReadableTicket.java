package com.marketing.web.dtos.ticket;

import com.marketing.web.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableTicket implements Serializable {

    private String id;

    private String title;

    private TicketStatus status;

    private String openerName;

    private Date addedTime;
}
