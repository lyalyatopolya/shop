package com.example.shop.model;

import com.example.shop.model.enums.OrderStatus;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "shop_Order")
@Table(name = "SHOP_ORDER")
public class Order extends AbstractPersistable<UUID> {

    @Column(name = "STATUS_COMMENT", length = 2000)
    private String statusComment;

    @Column(name = "ORDER_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.FORMING;

    @ManyToOne
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private Person person;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private Set<OrderProduct> orderProducts = new HashSet<>();

    public Set<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(Set<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public String getStatusComment() {
        return statusComment;
    }

    public void setStatusComment(String statusComment) {
        this.statusComment = statusComment;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
