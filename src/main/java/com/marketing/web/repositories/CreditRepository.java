package com.marketing.web.repositories;

import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    Optional<Credit> findByUser_Id(Long userId);

    Page<Credit> findAll(Pageable pageable);

    Optional<Credit> findByUuid(UUID uuid);

}
