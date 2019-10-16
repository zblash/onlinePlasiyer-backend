package com.marketing.web.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "city_id",referencedColumnName = "id")
    private City city;

    @OneToOne
    @JoinColumn(name = "state_id",referencedColumnName = "id")
    private State state;

    private String details;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }

}