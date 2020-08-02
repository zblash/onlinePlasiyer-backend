package com.marketing.web.services.ticket;

import com.marketing.web.models.Ticket;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;


public interface TicketService {

    Page<Ticket> findAll(int pageNumber, String sortBy, String sortType);

    Ticket findById(String id);

    Page<Ticket> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    Ticket findByUserAndUUid(User user,String uuid);

    Ticket create(Ticket ticket);

    Ticket update(String id, Ticket updatedTicket);

    void delete(Ticket ticket);
}
