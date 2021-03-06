package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "states")
public class State extends BaseModel {

    @NotBlank
    private String title;

    @NotNull
    private int code;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "city_id",referencedColumnName = "id")
    private City city;
}
