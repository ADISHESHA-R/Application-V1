package com.Shopping.Shopping.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String uniqueProductId;
    private String imageUrl;
}
