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
import java.util.UUID;

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
    public Ticket findByUUID(String uuid) {
        return ticketRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: "+uuid));
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
    public Ticket findByUserAndUUid(User user,String uuid) {
        return ticketRepository.findByUuidAndUser_Id(UUID.fromString(uuid),user.getId()).orElseThrow(() -> new ResourceNotFoundException("You have not ticket with id: "+uuid));
    }

    @Override
    public Ticket create(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket update(String uuid, Ticket updatedTicket) {
        Ticket ticket = findByUUID(uuid);
        ticket.setStatus(updatedTicket.getStatus());
        ticket.setTitle(updatedTicket.getTitle());
        ticket.setAddedTime(updatedTicket.getAddedTime());
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Ticket ticket) {
        ticketRepository.delete(ticket);
    }
}
