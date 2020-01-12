package com.marketing.web.repositories;

import com.marketing.web.models.UsersCredit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersCreditRepository extends JpaRepository<UsersCredit, Long> {

    Optional<UsersCredit> findByUuid(UUID uuid);

}
