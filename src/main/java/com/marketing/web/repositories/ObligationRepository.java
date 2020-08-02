package com.marketing.web.repositories;

import com.marketing.web.models.Merchant;
import com.marketing.web.models.Obligation;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ObligationRepository extends JpaRepository<Obligation, UUID> {

    Page<Obligation> findAll(Pageable pageable);

    Optional<Obligation> findByMerchant(Merchant merchant);
}
