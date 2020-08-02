package com.marketing.web.repositories;

import com.marketing.web.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID>, JpaSpecificationExecutor<Merchant> {

    Optional<Merchant> findByUser(User user);

    List<Merchant> findAllByActiveStatesContains(State state);

    List<Merchant> findAllByUserIn(List<User> users);
}
