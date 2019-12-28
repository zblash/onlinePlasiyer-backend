package com.marketing.web.repositories;

import com.marketing.web.models.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketReplyRepository extends JpaRepository<TicketReply,Long> {

    List<TicketReply> findAllByOrderByIdDesc();

    List<TicketReply> findAllByTicket_Id(Long ticketId);

    Optional<TicketReply> findByUuid(UUID uuid);

}
