package com.marketing.web.repositories;

import com.marketing.web.models.Obligation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ObligationRepository extends JpaRepository<Obligation, Long> {

    Page<Obligation> findAllByOrderByIdDesc(Pageable pageable);

}
