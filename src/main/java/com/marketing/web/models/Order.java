package com.marketing.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private double totalPrice;

    @NotNull
    private OrderStatus status;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<OrderItem> items;

    @OneToOne
    @JoinColumn(name = "seller_id",referencedColumnName = "id")
    private User seller;

    @OneToOne
    @JoinColumn(name = "buyer_id",referencedColumnName = "id")
    private User buyer;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date lastModifiedDate;

    public void addOrderItem(OrderItem orderItem){
        if (items == null){
            items = new ArrayList<>();
        }
        items.add(orderItem);
    }

    public void removeOrderItem(OrderItem orderItem){
        if (items != null){
            items.remove(orderItem);
        }

    }
}
