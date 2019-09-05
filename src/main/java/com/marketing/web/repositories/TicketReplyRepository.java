package com.marketing.web.repositories;

import com.marketing.web.models.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketReplyRepository extends JpaRepository<TicketReply,Long> {

    List<TicketReply> findAllByTicket_Id(Long ticketId);

}
