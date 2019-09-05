package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.ticket.ReadableTicket;
import com.marketing.web.dtos.ticket.ReadableTicketReply;
import com.marketing.web.dtos.ticket.WritableTicket;
import com.marketing.web.dtos.ticket.WritableTicketReply;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    TicketMapper INSTANCE = Mappers.getMapper( TicketMapper.class );

    default ReadableTicket ticketToReadableTicket(Ticket ticket){
        ReadableTicket readableTicket = new ReadableTicket();

        readableTicket.setId("ticket_"+ticket.getId());
        readableTicket.setOpenerName(ticket.getUser().getName());
        readableTicket.setStatus(ticket.getStatus());
        readableTicket.setTitle(ticket.getTitle());
        return readableTicket;
    }

    default Ticket writableTicketToTicket(WritableTicket writableTicket){
        Ticket ticket = new Ticket();
        ticket.setTitle(writableTicket.getTitle());
        ticket.setAddedTime(new Date());
        return ticket;
    }

    default ReadableTicketReply ticketReplyToReadableTicketReply(TicketReply ticketReply){
        ReadableTicketReply readableTicketReply = new ReadableTicketReply();
        readableTicketReply.setId("ticketReply_"+ticketReply.getId());
        readableTicketReply.setAddedTime(ticketReply.getAddedTime());
        readableTicketReply.setMessage(ticketReply.getMessage());
        readableTicketReply.setUsername(ticketReply.getUser().getUsername());
        return readableTicketReply;
    }

    default TicketReply writableTicketReplyToTicketReply(WritableTicketReply writableTicketReply){
        TicketReply ticketReply = new TicketReply();
        ticketReply.setMessage(writableTicketReply.getMessage());
        ticketReply.setAddedTime(new Date());
        return ticketReply;
    }
}
