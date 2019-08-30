package com.marketing.web.repositories;

import com.marketing.web.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {

    Optional<Invoice> findByOrder_Id(Long id);

    List<Invoice> findAllByBuyer_Id(Long id);

    List<Invoice> findAllBySeller_Id(Long id);

}
