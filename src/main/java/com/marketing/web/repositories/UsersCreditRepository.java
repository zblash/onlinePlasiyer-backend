package com.marketing.web.repositories;

import com.marketing.web.models.User;
import com.marketing.web.models.UsersCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersCreditRepository extends JpaRepository<UsersCredit, Long> {

    Optional<UsersCredit> findByUuid(UUID uuid);

    List<UsersCredit> findAllByMerchantOrCustomer(User merchant, User customer);

    Optional<UsersCredit> findByUuidAndMerchant(UUID uuid, User merchant);
}
