package com.marketing.web.services.ticket;

import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;

import java.util.List;

public interface TicketReplyService {

    List<TicketReply> findAll();

    TicketReply findById(String id);

    List<TicketReply> findAllByTicket(Ticket ticket);

    TicketReply create(TicketReply ticketReply);

    TicketReply update(String id, TicketReply updatedTicketReply);

    void delete(TicketReply ticketReply);
}
