package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.ticket.ReadableTicket;
import com.marketing.web.models.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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
}
