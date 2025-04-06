package com.example.shop.repository;


import com.example.shop.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderProductRepository extends JpaRepository<OrderProduct, UUID> {
}
