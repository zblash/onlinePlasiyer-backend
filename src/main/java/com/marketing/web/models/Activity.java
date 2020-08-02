package com.marketing.web.models;

import com.marketing.web.enums.ActivityType;
import com.marketing.web.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "activities")
public class Activity extends BaseModel {

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private PaymentType paymentType;

    private BigDecimal price;

    private BigDecimal paidPrice;

    private BigDecimal currentDebt;

    private BigDecimal currentReceivable;

    private BigDecimal creditLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private Customer customer;

    private LocalDate date;

}
