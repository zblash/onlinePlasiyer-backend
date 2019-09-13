package com.marketing.web.repositories;

import com.marketing.web.models.Product;
import com.marketing.web.models.ProductSpecify;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductSpecifyRepository extends JpaRepository<ProductSpecify,Long> {

    Optional<ProductSpecify> findByUuid(UUID uuid);

    Optional<ProductSpecify> findByUuidAndUser_Id(UUID uuid, Long userId);

    List<ProductSpecify> findAllByProductAndStatesInOrderByIdDesc(Product product, List<State> states);

    List<ProductSpecify> findAllByProductOrderByIdDesc(Product product);

    List<ProductSpecify> findAllByUserOrderByIdDesc(User user);

}
