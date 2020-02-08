package com.marketing.web.models;

import com.marketing.web.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "credits")
@Builder
public class Credit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private User merchant;

    @ManyToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private User customer;

    @NotNull
    private double totalDebt;

    @NotNull
    private double creditLimit;

    @NotNull
    private CreditType creditType;

    @OneToMany(mappedBy = "credit",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    @OrderBy("id desc")
    private List<CreditActivity> creditActivities;

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }

}
