package com.marketing.web.repositories;

import com.marketing.web.models.Merchant;
import com.marketing.web.models.ObligationActivity;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ObligationActivityRepository extends JpaRepository<ObligationActivity, UUID> {

    Page<ObligationActivity> findAllByMerchant(Merchant merchant, Pageable pageable);

    Optional<ObligationActivity> findByOrder(Order order);

    Optional<ObligationActivity> findByMerchant(Merchant merchant);

}
