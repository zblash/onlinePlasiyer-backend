package com.marketing.web.repositories;

import com.marketing.web.enums.TicketStatus;
import com.marketing.web.models.Ticket;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

    Page<Ticket> findAllByUser(User user, Pageable pageable);

    List<Ticket> findAllByUser_IdAndStatusOrderByIdDesc(Long userId, TicketStatus status);

    Optional<Ticket> findByUuidAndUser_Id(UUID uuid, Long userId);

    Optional<Ticket> findByUuid(UUID uuid);
}
