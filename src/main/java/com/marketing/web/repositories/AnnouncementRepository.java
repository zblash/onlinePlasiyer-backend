package com.marketing.web.repositories;

import com.marketing.web.models.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

    List<Announcement> findAllByLastDateAfterOrderByIdDesc(Date date);

    Page<Announcement> findAllByLastDateAfter(Date date, Pageable pageable);

    Page<Announcement> findAllByLastDateBefore(Date date, Pageable pageable);

    Page<Announcement> findAllByOrderByIdDesc(Pageable pageable);

}
