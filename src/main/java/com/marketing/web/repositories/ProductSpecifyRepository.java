package com.marketing.web.repositories;

import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductSpecifyRepository extends JpaRepository<ProductSpecify,Long> {

    Page<ProductSpecify> findAllByOrderByIdDesc(Pageable pageable);

    Optional<ProductSpecify> findByUuid(UUID uuid);

    Optional<ProductSpecify> findByUuidAndUser_Id(UUID uuid, Long userId);

    Page<ProductSpecify> findAllByProductAndStatesInOrderByIdDesc(Product product, List<State> states, Pageable pageable);

    Page<ProductSpecify> findAllByProductOrderByIdDesc(Product product, Pageable pageable);

    Page<ProductSpecify> findAllByUserOrderByIdDesc(User user, Pageable pageable);

}
