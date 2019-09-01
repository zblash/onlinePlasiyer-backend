package com.marketing.web.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "invoices")
public class Invoice extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
}
