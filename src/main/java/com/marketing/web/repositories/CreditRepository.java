package com.marketing.web.repositories;

import com.marketing.web.enums.CreditType;
import com.marketing.web.models.Customer;
import com.marketing.web.models.Merchant;
import com.marketing.web.models.User;
import com.marketing.web.models.Credit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {

    Page<Credit> findAllByCreditType(CreditType creditType, Pageable pageable);

    Page<Credit> findAllByCreditTypeAndMerchant(CreditType creditType, Merchant merchant, Pageable pageable);

    Page<Credit> findAllByCreditTypeAndCustomer(CreditType creditType, Customer customer, Pageable pageable);

    Optional<Credit> findByIdAndMerchant(UUID id, Merchant merchant);

    Page<Credit> findAllByCustomerAndMerchant(Customer customer, Merchant merchant, Pageable pageable);

    Page<Credit> findAllByCustomer(Customer customer, Pageable pageable);

    Page<Credit> findAllByMerchant(Merchant merchant, Pageable pageable);

    Optional<Credit> findByCustomerAndMerchant(Customer customer, Merchant merchant);

    Optional<Credit> findByCustomerAndCreditType(Customer customer, CreditType creditType);

    Page<Credit> findAllByCustomerOrMerchant(Customer customer, Merchant merchant, Pageable pageable);

    List<Credit> findAllByMerchantAndCustomer(Merchant merchant, Customer customer);

    Optional<Credit> findByCustomer(Customer customer);
}
