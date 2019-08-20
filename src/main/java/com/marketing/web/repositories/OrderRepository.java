package com.marketing.web.repositories;

import com.marketing.web.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findAllByBuyer_Id(Long id);

    List<Order> findAllBySeller_Id(Long id);
}
