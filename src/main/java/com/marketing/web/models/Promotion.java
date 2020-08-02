package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "promotions")
public class Promotion extends BaseModel {

    private String promotionText;

    private BigDecimal discountValue;

    private int discountUnit;
}
