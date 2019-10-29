package com.marketing.web.repositories;

import com.marketing.web.models.Invoice;
import com.marketing.web.models.Order;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    Page<Invoice> findAllByOrderByIdDesc(Pageable pageable);

    Optional<Invoice> findByOrder(Order order);

    Optional<Invoice> findByOrderAndBuyer_Id(Order order,Long buyerId);

    Optional<Invoice> findByOrderAndSeller_Id(Order order,Long sellerId);

    Page<Invoice> findAllByBuyer_IdOrderByIdDesc(Long id, Pageable pageable);

    Page<Invoice> findAllBySeller_IdOrderByIdDesc(Long id, Pageable pageable);

    Optional<Invoice> findByUuid(UUID uuid);

    Optional<Invoice> findByUuidAndBuyer_Id(UUID uuid, Long buyerId);

    Optional<Invoice> findByUuidAndSeller_Id(UUID uuid, Long sellerId);

}
