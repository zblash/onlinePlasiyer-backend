package com.marketing.web.dtos.notifications;

import com.marketing.web.dtos.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadableNotification extends DTO {

    private String id;

    private String title;

    private String message;
}
