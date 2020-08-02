package com.marketing.web.models;

import lombok.AllArgsConstructor;
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
@Table(name = "obligations")
public class Obligation extends BaseModel {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private Merchant merchant;

    @NotNull
    private BigDecimal debt;

    @NotNull
    private BigDecimal receivable;

}
