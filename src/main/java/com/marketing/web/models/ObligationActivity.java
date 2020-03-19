package com.marketing.web.models;

import com.marketing.web.enums.CreditActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "obligationactivities")
public class ObligationActivity extends BaseModel {

    private UUID uuid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "obligation_id",referencedColumnName = "id")
    private Obligation obligation;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Enumerated(EnumType.STRING)
    private CreditActivityType creditActivityType;

    @NotNull
    private double priceValue;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
