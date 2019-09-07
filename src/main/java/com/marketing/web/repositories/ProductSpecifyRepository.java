package com.marketing.web.repositories;

import com.marketing.web.models.ProductSpecify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductSpecifyRepository extends JpaRepository<ProductSpecify,Long> {

    Optional<ProductSpecify> findByUuid(UUID uuid);
}
