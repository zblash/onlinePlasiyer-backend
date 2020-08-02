package com.marketing.web.services.ticket;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.enums.TicketStatus;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.User;
import com.marketing.web.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Page<Ticket> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
        Page<Ticket> resultPage = ticketRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Ticket findById(String id) {
        return ticketRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"ticket",id.toString()));
    }

    @Override
    public Page<Ticket> findAllByUser(User user, int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
        Page<Ticket> resultPage = ticketRepository.findAllByUser(user, pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Ticket findByUserAndUUid(User user,String uuid) {
        return ticketRepository.findByIdAndUser_Id(UUID.fromString(uuid),user.getId()).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"ticket",uuid));
    }

    @Override
    public Ticket create(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket update(String id, Ticket updatedTicket) {
        Ticket ticket = findById(id);
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
