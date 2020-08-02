package com.marketing.web.repositories;

import com.marketing.web.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductSpecifyRepository extends JpaRepository<ProductSpecify, UUID> {

    Page<ProductSpecify> findAllBy(Pageable pageable);

    Optional<ProductSpecify> findByIdAndMerchant(UUID uuid, Merchant merchant);

    Page<ProductSpecify> findAllByProductAndStatesIn(Product product, List<State> states, Pageable pageable);

    Page<ProductSpecify> findAllByProduct(Product product, Pageable pageable);

    List<ProductSpecify> findAllByProduct(Product product);

    Page<ProductSpecify> findAllByProductAndMerchant(Product product, Merchant merchant, Pageable pageable);

    Page<ProductSpecify> findAllByMerchant(Merchant merchant, Pageable pageable);

    List<ProductSpecify> findAllByMerchant(Merchant merchant);

}
