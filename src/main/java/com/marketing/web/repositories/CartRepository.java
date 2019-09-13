package com.marketing.web.repositories;

import com.marketing.web.models.Cart;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByUuid(UUID uuid);

    Optional<Cart> findByUser(User user);
}
