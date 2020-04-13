package com.marketing.web.models;

import com.marketing.web.enums.ActivityType;
import com.marketing.web.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "activities")
public class Activity extends BaseModel {

    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private PaymentType paymentType;

    private double price;

    private double currentDebt;

    private double currentReceivable;

    private double creditLimit;

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