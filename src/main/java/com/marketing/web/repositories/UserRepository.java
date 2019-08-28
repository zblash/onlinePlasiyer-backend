package com.marketing.web.repositories;

import com.marketing.web.models.Role;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUserName(String userName);

    List<User> findAllByStatus(boolean status);

    List<User> findAllByRole(Role role);
}
