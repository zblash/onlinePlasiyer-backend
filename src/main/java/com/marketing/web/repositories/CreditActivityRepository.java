package com.marketing.web.repositories;

import com.marketing.web.enums.CreditActivityType;
import com.marketing.web.models.Credit;
import com.marketing.web.models.CreditActivity;
import com.marketing.web.models.Order;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditActivityRepository extends JpaRepository<CreditActivity, Long>, JpaSpecificationExecutor<CreditActivity> {

     Page<CreditActivity> findAllByCustomerOrMerchant(User customer, User merchant, Pageable pageable);

     Optional<CreditActivity> findByUuid(UUID uuid);

     Page<CreditActivity> findAllByCreditActivityType(CreditActivityType creditActivityType, Pageable pageable);

     List<CreditActivity> findAllByOrder(Order order);

     List<CreditActivity> findAllByCredit(Credit credit);

     void deleteByOrder(Order order);

    Page<CreditActivity> findAllByCustomerAndMerchant(User customer, User merchant, Pageable pageable);

    Page<CreditActivity> findAllByMerchantAndDateBetween(User merchant, LocalDate startDate, LocalDate lastDate, Pageable pageable);

    Page<CreditActivity> findAllByCustomerAndDateBetween(User customer, LocalDate startDate, LocalDate lastDate, Pageable pageable);
}
