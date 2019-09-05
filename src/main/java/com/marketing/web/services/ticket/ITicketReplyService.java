package com.marketing.web.services.ticket;

import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;

import java.util.List;

public interface ITicketReplyService {

    List<TicketReply> findAll();

    TicketReply findById(Long id);

    List<TicketReply> findAllByTicket(Ticket ticket);

    TicketReply create(TicketReply ticketReply);

    TicketReply update(Long id, TicketReply updatedTicketReply);

    void delete(TicketReply ticketReply);
}
