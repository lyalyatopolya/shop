package com.example.shop.model.entity;

import com.example.shop.model.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Entity(name = "shop_Order")
@Table(name = "SHOP_ORDER")
public class Order extends AbstractPersistable<UUID> {

    @Column(name = "STATUS_COMMENT", length = 2000)
    private String statusComment;

    @Column(name = "ORDER_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.FORMING;

    @NotNull(message = "Заказчик не указан")
    @ManyToOne
    @JoinColumn(name = "PERSON_ID", nullable = false)
    private Person person;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private Set<OrderProduct> orderProducts = new HashSet<>();
}
