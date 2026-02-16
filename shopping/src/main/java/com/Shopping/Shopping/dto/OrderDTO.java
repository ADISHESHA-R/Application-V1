package com.Shopping.Shopping.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private double amount;
    private LocalDateTime orderDate;
    private String email;
}
