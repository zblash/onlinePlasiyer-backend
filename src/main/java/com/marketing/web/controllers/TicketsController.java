package com.marketing.web.controllers;

import com.marketing.web.dtos.ticket.ReadableTicket;
import com.marketing.web.dtos.ticket.ReadableTicketReply;
import com.marketing.web.dtos.ticket.WritableTicket;
import com.marketing.web.dtos.ticket.WritableTicketReply;
import com.marketing.web.enums.TicketStatus;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;
import com.marketing.web.models.User;
import com.marketing.web.services.ticket.TicketReplyService;
import com.marketing.web.services.ticket.TicketReplyServiceImpl;
import com.marketing.web.services.ticket.TicketService;
import com.marketing.web.services.ticket.TicketServiceImpl;
import com.marketing.web.services.user.UserService;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.mappers.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketsController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketReplyService ticketReplyService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReadableTicket>> getAllTickets(){
        return ResponseEntity.ok(ticketService.findAll().stream()
                .map(TicketMapper::ticketToReadableTicket)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<ReadableTicketReply>> getTicketReplies(@PathVariable String id){
        User loggedInUser = userService.getLoggedInUser();
        Ticket ticket;

        if (loggedInUser.getRole().getName().equals("ROLE_ADMIN")){
            ticket = ticketService.findByUUID(id);
        }else{
            ticket = ticketService.findByUserAndUUid(loggedInUser,id);
        }

        return ResponseEntity.ok(ticketReplyService.findAllByTicket(ticket).stream()
                .map(TicketMapper::ticketReplyToReadableTicketReply)
                .collect(Collectors.toList()));
    }

    @PostMapping("/create")
    public ResponseEntity<ReadableTicket> createTicket(@RequestBody WritableTicket writableTicket){
        Ticket ticket = TicketMapper.writableTicketToTicket(writableTicket);
        ticket.setStatus(TicketStatus.OPN);
        ticket.setUser(userService.getLoggedInUser());
        return ResponseEntity.ok(TicketMapper.ticketToReadableTicket(ticketService.create(ticket)));
    }

    @PostMapping("/{id}/createReply")
    public ResponseEntity<ReadableTicketReply> createTicketReply(@PathVariable String id,
                                                                 @RequestBody WritableTicketReply writableTicketReply){
        User loggedInUser = userService.getLoggedInUser();
        Ticket ticket;

        if (loggedInUser.getRole().getName().equals("ROLE_ADMIN")){
            ticket = ticketService.findByUUID(id);
            if (ticket.getTicketReplies().size() <= 1){
                ticket.setStatus(TicketStatus.ANS);
                ticketService.update(ticket.getUuid().toString(),ticket);
            }
        }else{
            ticket = ticketService.findByUserAndUUid(loggedInUser,id);
        }
        TicketReply ticketReply =TicketMapper.writableTicketReplyToTicketReply(writableTicketReply);
        ticketReply.setUser(loggedInUser);
        ticketReply.setTicket(ticket);
        return ResponseEntity.ok(
                TicketMapper.ticketReplyToReadableTicketReply(ticketReplyService.create(ticketReply)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/changeStatus/{id}")
    public ResponseEntity<ReadableTicket> changeTicketStatus(@PathVariable String id, @RequestBody TicketStatus ticketStatus){
        Ticket ticket = ticketService.findByUUID(id);
        ticket.setStatus(ticketStatus);
        return ResponseEntity.ok(TicketMapper.ticketToReadableTicket(ticketService.update(id,ticket)));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ReadableTicket> updateTicket(@PathVariable String id,@RequestBody WritableTicket writableTicket){
        User loggedInUser = userService.getLoggedInUser();
        Ticket ticket;

        if (loggedInUser.getRole().getName().equals("ROLE_ADMIN")){
            ticket = ticketService.findByUUID(id);
        }else{
            ticket = ticketService.findByUserAndUUid(loggedInUser,id);
        }
        Ticket updatedTicket = TicketMapper.writableTicketToTicket(writableTicket);
        return ResponseEntity.ok(TicketMapper.ticketToReadableTicket(ticketService.update(id,updatedTicket)));
    }

}
