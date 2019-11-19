package com.marketing.web.repositories;

import com.marketing.web.models.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {

    Page<Announcement> findAllByLastDateAfterOrderByIdDesc(Date date, Pageable pageable);

    Page<Announcement> findAllByOrderByIdDesc(Pageable pageable);

    Optional<Announcement> findByUuid(UUID uuid);
}
