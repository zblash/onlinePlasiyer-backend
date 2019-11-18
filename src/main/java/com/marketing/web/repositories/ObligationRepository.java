package com.marketing.web.repositories;

import com.marketing.web.models.Obligation;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ObligationRepository extends JpaRepository<Obligation, Long> {

    Page<Obligation> findAllByOrderByIdDesc(Pageable pageable);

    Optional<Obligation> findByUuid(UUID uuid);

    List<Obligation> findAllByUserOrderByIdDesc(User user);

    Page<Obligation> findAllByUserOrderByIdDesc(User user,Pageable pageable);
}
