package com.marketing.web.controllers;

import com.marketing.web.dtos.ticket.ReadableTicket;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;
import com.marketing.web.services.ticket.TicketService;
import com.marketing.web.utils.mappers.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketsController {

    @Autowired
    private TicketService ticketService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReadableTicket>> getAllTickets(){
        return ResponseEntity.ok(ticketService.findAll().stream()
                .map(TicketMapper.INSTANCE::ticketToReadableTicket)
                .collect(Collectors.toList()));
    }


}
