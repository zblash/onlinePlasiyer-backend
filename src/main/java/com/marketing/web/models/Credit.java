package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "credits")
public class Credit extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private User payer;

    private User creditor;

    private double totalDebt;

    private double overdueDebt;
    
    private double notOverdueDebt;

    private double creditLimit;

}
