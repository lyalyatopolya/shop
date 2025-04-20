package com.example.shop.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.UUID;

@Getter
@Setter
@Entity(name = "shop_OrderProduct")
@Table(name = "SHOP_ORDER_PRODUCT")
public class OrderProduct extends AbstractPersistable<UUID> {

    @Column(name = "count_", nullable = false)
    private int count;

    @Transient
    private int countInStoreHouse = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @Column(name = "PRODUCT_ID", nullable = false)
    private UUID productId;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

}
