package com.marketing.web.services.ticket;

import com.marketing.web.enums.TicketStatus;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;
import com.marketing.web.models.User;
import com.marketing.web.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService implements ITicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: "+id));
    }

    @Override
    public List<Ticket> findAllByUser(User user) {
        return ticketRepository.findAllByUser_Id(user.getId());
    }

    @Override
    public List<Ticket> findAllByUserAndStatus(User user, TicketStatus status) {
        return ticketRepository.findAllByUser_IdAndStatus(user.getId(),status);
    }

    @Override
    public Ticket findByUserAndId(User user,Long id) {
        return ticketRepository.findByIdAndUser_Id(id,user.getId()).orElseThrow(() -> new ResourceNotFoundException("You have not ticket with id: "+id));
    }

    @Override
    public Ticket create(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket update(Long id, Ticket updatedTicket) {
        Ticket ticket = findById(id);
        ticket.setStatus(updatedTicket.getStatus());
        ticket.setTitle(updatedTicket.getTitle());
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Ticket ticket) {
        ticketRepository.delete(ticket);
    }
}
