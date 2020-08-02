package com.marketing.web.models;

import com.marketing.web.enums.CreditActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "creditactivities")
public class CreditActivity extends BaseModel {

    private BigDecimal priceValue;

    private BigDecimal currentDebt;

    private BigDecimal creditLimit;

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
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private Customer customer;

    private LocalDate date;
}
