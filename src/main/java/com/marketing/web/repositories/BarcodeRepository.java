package com.marketing.web.repositories;

import com.marketing.web.models.Barcode;
import com.marketing.web.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BarcodeRepository extends JpaRepository<Barcode,Long> {

    Optional<Barcode> findByUuid(UUID uuid);

    Optional<Barcode> findByBarcodeNo(String barcodeNo);

    List<Barcode> findAllByProduct(Product product);
}
