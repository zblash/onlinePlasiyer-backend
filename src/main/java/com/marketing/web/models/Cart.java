package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "carts")
public class Cart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<CartItem> items;


    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}