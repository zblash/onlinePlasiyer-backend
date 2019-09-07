package com.marketing.web.repositories;

import com.marketing.web.models.Invoice;
import com.marketing.web.models.Order;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    Optional<Invoice> findByOrder(Order order);

    List<Invoice> findAllByBuyer_Id(Long id);

    List<Invoice> findAllBySeller_Id(Long id);

    Optional<Invoice> findByUuid(UUID uuid);

}
