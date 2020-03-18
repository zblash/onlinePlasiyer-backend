package com.marketing.web.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "cities")
public class City extends BaseModel {

    private UUID uuid;

    @NotBlank
    private String title;

    @NotNull
    private int code;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }

}
