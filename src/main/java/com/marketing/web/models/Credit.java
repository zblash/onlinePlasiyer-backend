package com.marketing.web.models;

import com.marketing.web.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "credits")
@Builder
public class Credit extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private Customer customer;

    @NotNull
    private BigDecimal totalDebt;

    @NotNull
    private BigDecimal creditLimit;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CreditType creditType;

}
