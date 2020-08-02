package com.marketing.web.repositories;

import com.marketing.web.models.Customer;
import com.marketing.web.models.State;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByUser(User user);

    List<Customer> findAllByUserStateInAndUserStatus(List<State> states, boolean status);

    List<Customer> findAllByUserIn(List<User> users);
}
