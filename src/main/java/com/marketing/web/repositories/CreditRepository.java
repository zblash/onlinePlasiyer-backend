package com.marketing.web.repositories;

import com.marketing.web.enums.CreditType;
import com.marketing.web.models.User;
import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    Optional<Credit> findByUuid(UUID uuid);

    Page<Credit> findAllByCreditType(CreditType creditType, Pageable pageable);

    Page<Credit> findAllByMerchantOrCustomer(User merchant, User customer, Pageable pageable);

    Optional<Credit> findByUuidAndMerchant(UUID uuid, User merchant);

    Optional<Credit> findByCustomerAndMerchant(User customer, User merchant);

    Optional<Credit> findByCustomerAndCreditType(User customer, CreditType creditType);

    Page<Credit> findAllByCustomerOrMerchant(User customer, User merchant, Pageable pageable);

    List<Credit> findAllByMerchantAndCustomer(User merchant, User customer);
}
