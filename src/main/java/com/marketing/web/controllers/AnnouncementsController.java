package com.marketing.web.controllers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.announcement.ReadableAnnouncement;
import com.marketing.web.dtos.announcement.WritableAnnouncement;
import com.marketing.web.models.Announcement;
import com.marketing.web.services.announcement.AnnouncementService;
import com.marketing.web.services.storage.AmazonClient;
import com.marketing.web.utils.mappers.AnnouncementMapper;
import com.marketing.web.validations.ValidImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementsController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AmazonClient amazonClient;

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, null,  new CustomDateEditor(dateFormat, false));
    }

    @GetMapping
    public ResponseEntity<List<ReadableAnnouncement>> getAll(){
        List<Announcement> announcements = announcementService.findAllActives(new Date());
        return ResponseEntity.ok(announcements.stream()
                .map(AnnouncementMapper::announcementToReadableAnnouncement).collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<WrapperPagination<ReadableAnnouncement>> getAllWithInactives(@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String sortType){

        return ResponseEntity.ok(AnnouncementMapper.pagedAnnouncementListToWrapperReadableAnnouncement(announcementService.findAll(pageNumber, sortBy, sortType)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ReadableAnnouncement> createAnnouncement(@Valid WritableAnnouncement writableAnnouncement, @ValidImg @RequestParam(value="uploadfile", required = true) final MultipartFile uploadfile){

        Announcement announcement = new Announcement();
        announcement.setTitle(writableAnnouncement.getTitle());
        announcement.setMessage(writableAnnouncement.getMessage());
        announcement.setLastDate(writableAnnouncement.getLastDate());
        String fileUrl = amazonClient.uploadFile(uploadfile);
        announcement.setFileUrl(fileUrl);
        return new ResponseEntity<>(AnnouncementMapper.announcementToReadableAnnouncement(announcementService.create(announcement)),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReadableAnnouncement> updateAnnouncement(@PathVariable String id, @Valid WritableAnnouncement writableAnnouncement, @ValidImg @RequestParam(value="uploadfile", required = false) final MultipartFile uploadfile){

        Announcement announcement = announcementService.findByUUID(id);
        amazonClient.deleteFileFromS3Bucket(announcement.getFileUrl());
        announcement.setTitle(writableAnnouncement.getTitle());
        announcement.setMessage(writableAnnouncement.getMessage());
        announcement.setLastDate(writableAnnouncement.getLastDate());
        if (uploadfile != null && !uploadfile.isEmpty()) {
            amazonClient.deleteFileFromS3Bucket(announcement.getFileUrl());
            String fileUrl = amazonClient.uploadFile(uploadfile);
            announcement.setFileUrl(fileUrl);
        }
        return ResponseEntity.ok(AnnouncementMapper.announcementToReadableAnnouncement(announcementService.update(id,announcement)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReadableAnnouncement> deleteAnnouncement(@PathVariable String id){
        Announcement announcement = announcementService.findByUUID(id);
        amazonClient.deleteFileFromS3Bucket(announcement.getFileUrl());
        announcementService.delete(announcement);
        return ResponseEntity.ok(AnnouncementMapper.announcementToReadableAnnouncement(announcement));
    }

}
