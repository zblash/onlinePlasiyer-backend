package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.enums.UnitType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "orderitems")
public class OrderItem extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    private double price;

    private double unitPrice;

    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    private double recommendedRetailPrice;

    private String productName;

    private String productBarcode;

    private double productTax;

    private String productPhotoUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User seller;

    @NotNull
    private int quantity;

    private double totalPrice;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
