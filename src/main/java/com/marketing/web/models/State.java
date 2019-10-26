package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "states")
public class State implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    private String title;

    private int code;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "city_id",referencedColumnName = "id")
    private City city;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
