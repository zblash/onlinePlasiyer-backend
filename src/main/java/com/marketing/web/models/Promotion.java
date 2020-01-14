package com.marketing.web.models;

import com.marketing.web.enums.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "promotions")
public class Promotion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private PromotionType promotionType;

    private String promotionText;

    private double discountPercent;

    private int discountUnit;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
