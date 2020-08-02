package com.marketing.web.repositories;

import com.marketing.web.models.Notification;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByUser(User user);

}
