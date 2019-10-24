package com.marketing.web.dtos.websockets;

import com.marketing.web.dtos.notification.ReadableNotification;
import com.marketing.web.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperWsNotification implements Serializable {

    private User user;

    private ReadableNotification notification;

}
