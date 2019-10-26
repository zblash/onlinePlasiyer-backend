package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "notifications")
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    private String title;

    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
