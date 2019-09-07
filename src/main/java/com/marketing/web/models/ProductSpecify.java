package com.marketing.web.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketing.web.enums.UnitType;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_specifies")
public class ProductSpecify extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    private double totalPrice;

    private double unitPrice;

    private int quantity;

    private double contents;

    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    private double recommendedRetailPrice;

    @ManyToOne
    @JoinColumn(name = "product_id",referencedColumnName = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<State> states;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
