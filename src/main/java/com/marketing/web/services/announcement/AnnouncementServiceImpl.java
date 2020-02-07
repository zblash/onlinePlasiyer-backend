package com.marketing.web.services.announcement;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.Announcement;
import com.marketing.web.repositories.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    public List<Announcement> findAllActives(Date date) {
        return announcementRepository.findAllByLastDateAfterOrderByIdDesc(date);
    }

    @Override
    public Page<Announcement> findAll(int pageNumber, String sortBy, String sortType) {
        PageRequest pageRequest = getPageRequest(pageNumber, sortBy, sortType);
        Page<Announcement> resultPage = announcementRepository.findAll(pageRequest);
        if (pageNumber > resultPage.getTotalPages() && pageNumber != 1) {
            throw new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"page", String.valueOf(pageNumber));
        }
        return resultPage;
    }

    @Override
    public Announcement findById(Long id) {
        return announcementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"announcement",id.toString()));
    }

    @Override
    public Announcement findByUUID(String uuid) {
        return announcementRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"announcement",uuid));
    }

    @Override
    public Announcement create(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Override
    public Announcement update(String uuid, Announcement updatedAnnouncement) {
        Announcement announcement = findByUUID(uuid);
        announcement.setLastDate(updatedAnnouncement.getLastDate());
        announcement.setFileUrl(updatedAnnouncement.getFileUrl());
        announcement.setMessage(updatedAnnouncement.getMessage());
        announcement.setTitle(updatedAnnouncement.getTitle());
        return announcementRepository.save(announcement);
    }

    @Override
    public void delete(Announcement announcement) {
        announcementRepository.delete(announcement);
    }

    private PageRequest getPageRequest(int pageNumber, String sortBy, String sortType){
        return PageRequest.of(pageNumber-1,15, Sort.by(Sort.Direction.fromString(sortType.toUpperCase()),sortBy));
    }
}
