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
@Table(name = "invoices")
public class Invoice extends BaseModel {

    private UUID uuid;

    @NotNull
    private double totalPrice;

    @NotNull
    private double paidPrice;

    private double unPaidPrice;

    private double discount;

    @OneToOne
    @JoinColumn(name = "seller_id",referencedColumnName = "id")
    private User seller;

    @OneToOne
    @JoinColumn(name = "buyer_id",referencedColumnName = "id")
    private User buyer;

    @OneToOne
    @JoinColumn(name = "order_id",referencedColumnName = "id")
    private Order order;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
