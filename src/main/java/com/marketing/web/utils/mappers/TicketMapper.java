package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.ticket.ReadableTicket;
import com.marketing.web.dtos.ticket.ReadableTicketReply;
import com.marketing.web.dtos.ticket.WritableTicket;
import com.marketing.web.dtos.ticket.WritableTicketReply;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.stream.Collectors;

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
            readableTicket.setAddedTime(ticket.getAddedTime());
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
            ticket.setImportanceLevel(writableTicket.getImportanceLevel());
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
    public static WrapperPagination<ReadableTicket> pagedTicketListToWrapperReadableTicket(Page<Ticket> pagedTicket){
        if (pagedTicket == null) {
            return null;
        } else {
            WrapperPagination<ReadableTicket> wrapperReadableTicket = new WrapperPagination<>();
            wrapperReadableTicket.setKey("tickets");
            wrapperReadableTicket.setTotalPage(pagedTicket.getTotalPages());
            wrapperReadableTicket.setPageNumber(pagedTicket.getNumber()+1);
            if (pagedTicket.hasPrevious()) {
                wrapperReadableTicket.setPreviousPage(pagedTicket.getNumber());
            }
            if (pagedTicket.hasNext()) {
                wrapperReadableTicket.setNextPage(pagedTicket.getNumber()+2);
            }
            wrapperReadableTicket.setFirst(pagedTicket.isFirst());
            wrapperReadableTicket.setLast(pagedTicket.isLast());
            wrapperReadableTicket.setElementCountOfPage(pagedTicket.getNumberOfElements());
            wrapperReadableTicket.setTotalElements(pagedTicket.getTotalElements());
            wrapperReadableTicket.setValues(pagedTicket.getContent().stream()
                    .map(TicketMapper::ticketToReadableTicket).collect(Collectors.toList()));
            return wrapperReadableTicket;
        }
    }
}
