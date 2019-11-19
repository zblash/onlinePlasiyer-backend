package com.marketing.web.services.announcement;

import com.marketing.web.models.Announcement;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface AnnouncementService {

    Page<Announcement> findAllActives(Date date, int pageNumber);

    Announcement findById(Long id);

    Announcement findByUUID(String uuid);

    Announcement create(Announcement announcement);

    Announcement update(String uuid,Announcement updatedAnnouncement);

    void delete(Announcement announcement);
}
