package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.WrapperPagination;
import com.marketing.web.dtos.announcement.ReadableAnnouncement;
import com.marketing.web.models.Announcement;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

public final class AnnouncementMapper {

    public static ReadableAnnouncement announcementToReadableAnnouncement(Announcement announcement){
        if (announcement == null) {
            return null;
        } else {
            ReadableAnnouncement readableAnnouncement = new ReadableAnnouncement();
            readableAnnouncement.setId(announcement.getUuid().toString());
            readableAnnouncement.setFileUrl(announcement.getFileUrl());
            readableAnnouncement.setMessage(announcement.getMessage());
            readableAnnouncement.setTitle(announcement.getTitle());
            readableAnnouncement.setLastDate(announcement.getLastDate());
            return readableAnnouncement;
        }
    }
    public static WrapperPagination<ReadableAnnouncement> pagedAnnouncementListToWrapperReadableAnnouncement(Page<Announcement> pagedAnnouncement){
        if (pagedAnnouncement == null) {
            return null;
        } else {
            WrapperPagination<ReadableAnnouncement> wrapperReadableAnnouncement = new WrapperPagination<>();
            wrapperReadableAnnouncement.setKey("announcements");
            wrapperReadableAnnouncement.setTotalPage(pagedAnnouncement.getTotalPages());
            wrapperReadableAnnouncement.setPageNumber(pagedAnnouncement.getNumber()+1);
            if (pagedAnnouncement.hasPrevious()) {
                wrapperReadableAnnouncement.setPreviousPage(pagedAnnouncement.getNumber());
            }
            if (pagedAnnouncement.hasNext()) {
                wrapperReadableAnnouncement.setNextPage(pagedAnnouncement.getNumber()+2);
            }
            wrapperReadableAnnouncement.setFirst(pagedAnnouncement.isFirst());
            wrapperReadableAnnouncement.setLast(pagedAnnouncement.isLast());
            wrapperReadableAnnouncement.setElementCountOfPage(15);
            wrapperReadableAnnouncement.setTotalElements(pagedAnnouncement.getTotalElements());
            wrapperReadableAnnouncement.setValues(pagedAnnouncement.getContent().stream()
                    .map(AnnouncementMapper::announcementToReadableAnnouncement).collect(Collectors.toList()));
            return wrapperReadableAnnouncement;
        }
    }
}
