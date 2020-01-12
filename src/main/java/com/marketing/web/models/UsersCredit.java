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
@Table(name = "userscredits")
public class UsersCredit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private User merchant;

    @OneToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private User customer;

    @NotNull
    private double totalDebt;

    @NotNull
    private double creditLimit;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }

}
