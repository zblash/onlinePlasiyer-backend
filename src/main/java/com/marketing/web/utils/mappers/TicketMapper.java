package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.ticket.ReadableTicket;
import com.marketing.web.dtos.ticket.ReadableTicketReply;
import com.marketing.web.dtos.ticket.WritableTicket;
import com.marketing.web.dtos.ticket.WritableTicketReply;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;

import java.util.Date;

public final class TicketMapper {

    public static ReadableTicket ticketToReadableTicket(Ticket ticket){
        if (ticket == null) {
            return null;
        } else {
            ReadableTicket readableTicket = new ReadableTicket();
            readableTicket.setId(ticket.getUuid().toString());
            readableTicket.setOpenerName(ticket.getUser().getName());
            readableTicket.setStatus(ticket.getStatus());
            readableTicket.setTitle(ticket.getTitle());
            return readableTicket;
        }
    }

    public static Ticket writableTicketToTicket(WritableTicket writableTicket) {
        if (writableTicket == null) {
            return null;
        } else {
            Ticket ticket = new Ticket();
            ticket.setTitle(writableTicket.getTitle());
            ticket.setAddedTime(new Date());
            return ticket;
        }
    }

    public static ReadableTicketReply ticketReplyToReadableTicketReply(TicketReply ticketReply){
        if (ticketReply == null) {
            return null;
        } else {
            ReadableTicketReply readableTicketReply = new ReadableTicketReply();
            readableTicketReply.setId(ticketReply.getUuid().toString());
            readableTicketReply.setAddedTime(ticketReply.getAddedTime());
            readableTicketReply.setMessage(ticketReply.getMessage());
            readableTicketReply.setUsername(ticketReply.getUser().getUsername());
            return readableTicketReply;
        }
    }

    public static TicketReply writableTicketReplyToTicketReply(WritableTicketReply writableTicketReply){
        if (writableTicketReply == null) {
            return null;
        } else {
            TicketReply ticketReply = new TicketReply();
            ticketReply.setMessage(writableTicketReply.getMessage());
            ticketReply.setAddedTime(new Date());
            return ticketReply;
        }
    }
}
