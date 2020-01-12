package com.marketing.web.repositories;

import com.marketing.web.models.SystemCredit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SystemCreditRepository extends JpaRepository<SystemCredit, Long> {

    Optional<SystemCredit> findByUser_Id(Long userId);

    Page<SystemCredit> findAll(Pageable pageable);

    Optional<SystemCredit> findByUuid(UUID uuid);

}
