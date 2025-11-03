package com.Shopping.Shopping.repository;

import com.Shopping.Shopping.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Orders findByRazorpayOrderId(String orderId);
}
