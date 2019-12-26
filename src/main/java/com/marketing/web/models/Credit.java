package com.marketing.web.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "credits",uniqueConstraints={@UniqueConstraint(columnNames={"user_id"})})
public class Credit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @NotNull
    private double totalDebt;

    @NotNull
    private double creditLimit;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
