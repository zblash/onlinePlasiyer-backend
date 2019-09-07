package com.marketing.web.models;

import com.marketing.web.enums.OrderStatus;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "orders")
public class Order extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @NotNull
    private double totalPrice;

    @NotNull
    private OrderStatus status;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    @OneToOne
    @JoinColumn(name = "seller_id",referencedColumnName = "id")
    private User seller;

    @OneToOne
    @JoinColumn(name = "buyer_id",referencedColumnName = "id")
    private User buyer;

    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @Temporal(TemporalType.DATE)
    private Date waybillDate;

    public void addOrderItem(OrderItem orderItem){
        if (orderItems == null){
            orderItems = new ArrayList<>();
        }
        orderItems.add(orderItem);
    }

    public void removeOrderItem(OrderItem orderItem){
        if (orderItems != null){
            orderItems.remove(orderItem);
        }

    }

    @PrePersist
    public void autofill() {
        this.setUuid(UUID.randomUUID());
    }
}
