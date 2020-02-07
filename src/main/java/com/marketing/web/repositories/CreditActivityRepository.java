package com.marketing.web.repositories;

import com.marketing.web.enums.CreditActivityType;
import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditActivityRepository extends JpaRepository<CreditActivity, Long> {

     Optional<CreditActivity> findByUuid(UUID uuid);

     Page<CreditActivity> findAllByCreditActivityType(CreditActivityType creditActivityType, Pageable pageable);

     List<CreditActivity> findAllByOrder(Order order);

     List<CreditActivity> findAllByCredit(Credit credit);

     void deleteByOrder(Order order);
}
