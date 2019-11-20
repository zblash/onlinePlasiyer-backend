package com.marketing.web.controllers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.announcement.ReadableAnnouncement;
import com.marketing.web.dtos.announcement.WritableAnnouncement;
import com.marketing.web.models.Announcement;
import com.marketing.web.services.announcement.AnnouncementService;
import com.marketing.web.services.storage.StorageService;
import com.marketing.web.utils.mappers.AnnouncementMapper;
import com.marketing.web.validations.ValidImg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementsController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private StorageService storageService;

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, null,  new CustomDateEditor(dateFormat, false));
    }

    @GetMapping
    public ResponseEntity<WrapperPagination<ReadableAnnouncement>> getAll(@RequestParam(required = false) Integer pageNumber){
        if (pageNumber == null){
            pageNumber=1;
        }
        return ResponseEntity.ok(AnnouncementMapper.pagedAnnouncementListToWrapperReadableAnnouncement(announcementService.findAllActives(new Date(),pageNumber)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<WrapperPagination<ReadableAnnouncement>> getAllWithInactives(@RequestParam(required = false) Integer pageNumber){
        if (pageNumber == null){
            pageNumber=1;
        }
        return ResponseEntity.ok(AnnouncementMapper.pagedAnnouncementListToWrapperReadableAnnouncement(announcementService.findAll(pageNumber)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ReadableAnnouncement> createAnnouncement(@Valid WritableAnnouncement writableAnnouncement, @ValidImg @RequestParam(value="uploadfile", required = true) final MultipartFile uploadfile, HttpServletRequest request){

        Announcement announcement = new Announcement();
        announcement.setTitle(writableAnnouncement.getTitle());
        announcement.setMessage(writableAnnouncement.getMessage());
        announcement.setLastDate(writableAnnouncement.getLastDate());
        String fileName = storageService.store(uploadfile);
        announcement.setFileUrl(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()+"/photos/"+fileName);
        return ResponseEntity.ok(AnnouncementMapper.announcementToReadableAnnouncement(announcementService.create(announcement)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ReadableAnnouncement> updateAnnouncement(@PathVariable String id, @Valid WritableAnnouncement writableAnnouncement, @ValidImg @RequestParam(value="uploadfile", required = false) final MultipartFile uploadfile, HttpServletRequest request){

        Announcement announcement = announcementService.findByUUID(id);
        announcement.setTitle(writableAnnouncement.getTitle());
        announcement.setMessage(writableAnnouncement.getMessage());
        announcement.setLastDate(writableAnnouncement.getLastDate());
        if (uploadfile != null && !uploadfile.isEmpty()) {
            String fileName = storageService.store(uploadfile);
            announcement.setFileUrl(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/photos/" + fileName);
        }
        return ResponseEntity.ok(AnnouncementMapper.announcementToReadableAnnouncement(announcementService.update(id,announcement)));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ReadableAnnouncement> deleteProduct(@PathVariable String id){
        Announcement announcement = announcementService.findByUUID(id);
        announcementService.delete(announcement);
        return ResponseEntity.ok(AnnouncementMapper.announcementToReadableAnnouncement(announcement));
    }

}
