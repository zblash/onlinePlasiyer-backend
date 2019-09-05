package com.marketing.web.repositories;

import com.marketing.web.models.Role;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByUsername(String username);

    List<User> findAllByStatus(boolean status);

    List<User> findAllByRole(Role role);

    List<User> findAllByRoleAndStatus(Role role,boolean status);
}
