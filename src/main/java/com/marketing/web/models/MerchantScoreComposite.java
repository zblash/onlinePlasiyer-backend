package com.marketing.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class MerchantScoreComposite implements Serializable {

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;

}