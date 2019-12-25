package com.marketing.web.services.ticket;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.TicketReply;
import com.marketing.web.repositories.TicketReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TicketReplyServiceImpl implements TicketReplyService {

    @Autowired
    private TicketReplyRepository ticketReplyRepository;

    @Override
    public List<TicketReply> findAll() {
        return ticketReplyRepository.findAllByOrderByIdDesc();
    }

    @Override
    public TicketReply findById(Long id) {
        return ticketReplyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket Reply not found with id: "+id));
    }

    @Override
    public TicketReply findByUUID(String uuid) {
        return ticketReplyRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("Ticket Reply not found with id: "+uuid));
    }

    @Override
    public List<TicketReply> findAllByTicket(Ticket ticket) {
        return ticketReplyRepository.findAllByTicket_Id(ticket.getId());
    }

    @Override
    public TicketReply create(TicketReply ticketReply) {
        return ticketReplyRepository.save(ticketReply);
    }

    @Override
    public TicketReply update(Long id, TicketReply updatedTicketReply) {
        TicketReply ticketReply = findById(id);
        ticketReply.setMessage(updatedTicketReply.getMessage());
        ticketReply.setTicket(updatedTicketReply.getTicket());
        ticketReply.setAddedTime(updatedTicketReply.getAddedTime());
        return ticketReplyRepository.save(ticketReply);
    }

    @Override
    public void delete(TicketReply ticketReply) {
        ticketReplyRepository.delete(ticketReply);
    }
}
