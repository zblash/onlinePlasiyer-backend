package com.marketing.web.repositories;

import com.marketing.web.enums.TicketStatus;
import com.marketing.web.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

    List<Ticket> findAllByOrderByIdDesc();

    List<Ticket> findAllByUser_IdOrderByIdDesc(Long id);

    List<Ticket> findAllByUser_IdAndStatusOrderByIdDesc(Long userId, TicketStatus status);

    Optional<Ticket> findByUuidAndUser_Id(UUID uuid, Long userId);

    Optional<Ticket> findByUuid(UUID uuid);
}
