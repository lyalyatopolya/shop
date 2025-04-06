package com.example.shop.model;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.UUID;

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

    public int getCountInStoreHouse() {
        return countInStoreHouse;
    }

    public void setCountInStoreHouse(int countInStoreHouse) {
        this.countInStoreHouse = countInStoreHouse;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
