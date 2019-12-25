package com.marketing.web.services.ticket;

import com.marketing.web.enums.TicketStatus;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TicketService {

    Page<Ticket> findAll(int pageNumber, String sortBy, String sortType);

    Ticket findById(Long id);

    Ticket findByUUID(String uuid);

    Page<Ticket> findAllByUser(User user, int pageNumber, String sortBy, String sortType);

    Ticket findByUserAndUUid(User user,String uuid);

    Ticket create(Ticket ticket);

    Ticket update(String uuid, Ticket updatedTicket);

    void delete(Ticket ticket);
}
