package com.marketing.web.services.ticket;

import com.marketing.web.models.Ticket;
import com.marketing.web.models.User;

import java.util.List;

public interface ITicketService {

    List<Ticket> findAll();

    Ticket findById(Long id);

    List<Ticket> findAllByUser(User user);

    List<Ticket> findAllByUserAndStatus(User user,boolean status);

    Ticket findByUserAndId(User user,Long id);

    Ticket create(Ticket ticket);

    Ticket update(Long id, Ticket updatedTicket);

    void delete(Ticket ticket);
}
