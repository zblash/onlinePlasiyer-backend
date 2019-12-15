package com.marketing.web.repositories;

import com.marketing.web.models.Notification;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findAllByUser(User user);

    Optional<Notification> findByUuid(UUID uuid);
}
