package com.marketing.web.dtos.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableAnnouncement implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String title;

    @NotBlank(message = "{validation.notBlank}")
    private String message;

    @NotNull(message = "{validation.notNull}")
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date lastDate;
}
