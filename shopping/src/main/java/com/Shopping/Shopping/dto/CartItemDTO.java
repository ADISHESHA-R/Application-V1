package com.Shopping.Shopping.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private ProductDTO product;
    private int quantity;
    private double subtotal;
}
