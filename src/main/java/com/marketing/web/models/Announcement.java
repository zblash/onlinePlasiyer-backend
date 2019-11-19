package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "announcements")
public class Announcement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    private String title;

    private String message;

    private String fileUrl;

    @Temporal(TemporalType.DATE)
    private Date lastDate;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
