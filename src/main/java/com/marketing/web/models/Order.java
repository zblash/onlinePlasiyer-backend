package com.marketing.web.models;

import com.marketing.web.enums.OrderStatus;
import com.marketing.web.enums.PaymentOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "orders")
public class Order extends BaseModel  {

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentOption paymentType;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    @OrderBy("id desc")
    private List<OrderItem> orderItems;

    @OneToOne
    @JoinColumn(name = "merchant_id",referencedColumnName = "id")
    private Merchant merchant;

    @OneToOne
    @JoinColumn(name = "customer_id",referencedColumnName = "id")
    private Customer customer;

    private LocalDate orderDate;

    private LocalDate waybillDate;

    private boolean commentable;

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
}
