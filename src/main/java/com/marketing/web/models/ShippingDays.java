package com.marketing.web.models;

import com.marketing.web.enums.DaysOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "shippingdays")
public class ShippingDays extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "DayOfWeek", joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.STRING)
    private List<DaysOfWeek> days;

    @OneToOne
    @JoinColumn(name = "state_id", referencedColumnName = "id")
    private State state;

}
