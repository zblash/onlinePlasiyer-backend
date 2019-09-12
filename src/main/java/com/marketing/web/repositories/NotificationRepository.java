package com.marketing.web.repositories;

import com.marketing.web.models.Notification;
import com.marketing.web.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findAllByUser(User user);
}
