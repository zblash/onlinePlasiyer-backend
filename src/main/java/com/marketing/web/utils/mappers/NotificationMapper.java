package com.marketing.web.utils.mappers;

import com.marketing.web.dtos.notification.ReadableNotification;
import com.marketing.web.models.Notification;

public final class NotificationMapper {

    public static ReadableNotification notificationToReadableNotification(Notification notification){
        if (notification == null) {
            return null;
        } else {
            ReadableNotification readableNotification = new ReadableNotification();
            readableNotification.setId(notification.getId().toString());
            readableNotification.setMessage(notification.getMessage());
            readableNotification.setTitle(notification.getTitle());
            readableNotification.setUserId(notification.getUser().getId().toString());
            readableNotification.setUserName(notification.getUser().getUsername());
            return readableNotification;
        }
    }

}
