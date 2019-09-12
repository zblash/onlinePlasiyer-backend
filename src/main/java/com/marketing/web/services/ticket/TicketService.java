package com.marketing.web.services.ticket;

import com.marketing.web.enums.TicketStatus;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.User;

import java.util.List;

public interface TicketService {

    List<Ticket> findAll();

    Ticket findById(Long id);

    Ticket findByUUID(String uuid);

    List<Ticket> findAllByUser(User user);

    List<Ticket> findAllByUserAndStatus(User user, TicketStatus status);

    Ticket findByUserAndUUid(User user,String uuid);

    Ticket create(Ticket ticket);

    Ticket update(String uuid, Ticket updatedTicket);

    void delete(Ticket ticket);
}
