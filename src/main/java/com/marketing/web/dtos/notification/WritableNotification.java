package com.marketing.web.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableNotification implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String title;

    @NotBlank(message = "{validation.notBlank}")
    private String message;

    @NotBlank(message = "{validation.notBlank}")
    private String userId;
}
