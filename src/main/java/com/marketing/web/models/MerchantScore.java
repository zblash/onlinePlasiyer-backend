package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "merchant_scores")
public class MerchantScore implements Serializable {

    @EmbeddedId
    private MerchantScoreComposite merchantScoreComposite;

    private double score;


}
