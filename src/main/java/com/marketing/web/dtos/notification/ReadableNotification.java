package com.marketing.web.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableNotification implements Serializable {

    private String id;

    private String title;

    private String message;

    private String userId;
}
