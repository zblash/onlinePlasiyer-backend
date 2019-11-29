package com.marketing.web.repositories;

import com.marketing.web.models.Role;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findAllByOrderByIdDesc();

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByStatusOrderByIdDesc(boolean status);

    List<User> findAllByRoleOrderByIdDesc(Role role);

    List<User> findAllByRoleAndStatusOrderByIdDesc(Role role,boolean status);

    Optional<User> findByUuid(UUID uuid);

    Optional<User> findByUsernameOrEmail(String username,String email);
}
