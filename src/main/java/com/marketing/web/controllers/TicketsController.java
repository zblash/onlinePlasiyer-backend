package com.marketing.web.controllers;

import com.marketing.web.dtos.common.WrapperPagination;
import com.marketing.web.dtos.ticket.ReadableTicket;
import com.marketing.web.dtos.ticket.ReadableTicketReply;
import com.marketing.web.dtos.ticket.WritableTicket;
import com.marketing.web.dtos.ticket.WritableTicketReply;
import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.TicketStatus;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;
import com.marketing.web.models.User;
import com.marketing.web.services.ticket.TicketReplyService;
import com.marketing.web.services.ticket.TicketService;
import com.marketing.web.services.user.UserService;
import com.marketing.web.utils.mappers.TicketMapper;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableTicket>> getAllTickets(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType){
        User user = userService.getLoggedInUser();
        RoleType role = UserMapper.roleToRoleType(user.getRole());
        if (role.equals(RoleType.ADMIN)){
            return ResponseEntity.ok(TicketMapper.pagedTicketListToWrapperReadableTicket(ticketService.findAll(pageNumber, sortBy, sortType)));
        }
        return ResponseEntity.ok(TicketMapper.pagedTicketListToWrapperReadableTicket(ticketService.findAllByUser(user, pageNumber, sortBy, sortType)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadableTicket> getTicketById(@PathVariable String id){
        User loggedInUser = userService.getLoggedInUser();
        Ticket ticket;

        if (loggedInUser.getRole().getName().equals("ROLE_ADMIN")){
            ticket = ticketService.findByUUID(id);
        }else{
            ticket = ticketService.findByUserAndUUid(loggedInUser,id);
        }
        return ResponseEntity.ok(TicketMapper.ticketToReadableTicket(ticket));
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

    @PostMapping
    public ResponseEntity<ReadableTicket> createTicket(@RequestBody WritableTicket writableTicket){
        User user = userService.getLoggedInUser();
        Ticket ticket = TicketMapper.writableTicketToTicket(writableTicket);
        ticket.setStatus(TicketStatus.OPN);
        ticket.setUser(user);
        Ticket savedTicket = ticketService.create(ticket);
        TicketReply ticketReply = new TicketReply();
        ticketReply.setTicket(savedTicket);
        ticketReply.setAddedTime(new Date());
        ticketReply.setUser(user);
        ticketReply.setMessage(writableTicket.getMessage());
        ticketReplyService.create(ticketReply);
        return new ResponseEntity<>(TicketMapper.ticketToReadableTicket(savedTicket), HttpStatus.CREATED);
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
        TicketReply ticketReply = TicketMapper.writableTicketReplyToTicketReply(writableTicketReply);
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}")
    public ResponseEntity<ReadableTicket> updateTicket(@PathVariable String id,@RequestBody WritableTicket writableTicket){
        Ticket updatedTicket = TicketMapper.writableTicketToTicket(writableTicket);
        return ResponseEntity.ok(TicketMapper.ticketToReadableTicket(ticketService.update(id,updatedTicket)));
    }

}
