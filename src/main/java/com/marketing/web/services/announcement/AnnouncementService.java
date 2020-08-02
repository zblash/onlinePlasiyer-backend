package com.marketing.web.services.announcement;

import com.marketing.web.models.Announcement;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface AnnouncementService {

    List<Announcement> findAllActives(Date date);

    Page<Announcement> findAllActives(int pageNumber, String sortBy, String sortType);

    Page<Announcement> findAllInActives(int pageNumber, String sortBy, String sortType);

    Page<Announcement> findAll(int pageNumber, String sortBy, String sortType);

    Announcement findById(String id);

    Announcement create(Announcement announcement);

    Announcement update(String uuid,Announcement updatedAnnouncement);

    void delete(Announcement announcement);

}
