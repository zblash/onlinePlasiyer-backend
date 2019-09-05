package com.marketing.web.repositories;

import com.marketing.web.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

    List<Ticket> findAllByUser_Id(Long id);

    List<Ticket> findAllByUser_IdAndStatus(Long userId,boolean status);

    Optional<Ticket> findByIdAndUser_Id(Long id, Long userId);
}
