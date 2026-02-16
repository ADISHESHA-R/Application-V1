package com.Shopping.Shopping.repository;

import com.Shopping.Shopping.model.Orders;
import com.Shopping.Shopping.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Orders findByRazorpayOrderId(String orderId);
    List<Orders> findByUser(User user);
}
