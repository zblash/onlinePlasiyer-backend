package com.marketing.web.models;

import com.marketing.web.enums.CreditActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "obligationactivities")
public class ObligationActivity extends BaseModel {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated(EnumType.STRING)
    private CreditActivityType creditActivityType;

    @NotNull
    private BigDecimal priceValue;

    @NotNull
    private BigDecimal totalDebt;

    @NotNull
    private BigDecimal totalReceivable;
}
