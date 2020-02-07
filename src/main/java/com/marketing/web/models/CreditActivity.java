package com.marketing.web.models;

import com.marketing.web.enums.CreditActivityType;
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
@Table(name = "creditactivities")
public class CreditActivity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    private double priceValue;

    private CreditActivityType creditActivityType;

    @ManyToOne
    @JoinColumn(name = "credit_id", referencedColumnName = "id")
    private Credit credit;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
