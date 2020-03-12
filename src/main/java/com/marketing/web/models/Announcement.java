package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "announcements")
public class Announcement extends BaseModel {

    private UUID uuid;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotBlank
    private String fileUrl;

    @Temporal(TemporalType.DATE)
    private Date lastDate;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
