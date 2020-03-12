package com.marketing.web.models;

import com.marketing.web.enums.CreditActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "creditactivities")
public class CreditActivity extends BaseModel {

    private UUID uuid;

    private double priceValue;

    private double currentDebt;

    private double creditLimit;

    @Enumerated(EnumType.STRING)
    private CreditActivityType creditActivityType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_id", referencedColumnName = "id")
    private Credit credit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private User merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private User customer;

    private LocalDate date;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
