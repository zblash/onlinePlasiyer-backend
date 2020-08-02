package com.marketing.web.repositories;

import com.marketing.web.models.Cart;
import com.marketing.web.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart,UUID> {

    Optional<Cart> findByCustomer(Customer customer);
}
