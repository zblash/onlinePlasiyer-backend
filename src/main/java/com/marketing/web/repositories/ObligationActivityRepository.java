package com.marketing.web.repositories;

import com.marketing.web.models.Obligation;
import com.marketing.web.models.ObligationActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ObligationActivityRepository extends JpaRepository<ObligationActivity, Long> {

    Page<ObligationActivity> findAllByObligation(Obligation obligation, Pageable pageable);

    Optional<ObligationActivity> findByUuid(UUID uuid);

}
