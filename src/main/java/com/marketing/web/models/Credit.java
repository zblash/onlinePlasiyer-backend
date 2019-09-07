package com.marketing.web.models;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "credits")
public class Credit extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    private User payer;

    private User creditor;

    private double totalDebt;

    private double overdueDebt;
    
    private double notOverdueDebt;

    private double creditLimit;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
