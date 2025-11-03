package com.Shopping.Shopping.service;

import com.Shopping.Shopping.model.Product;
import com.Shopping.Shopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
    }

    @Test
    void testSearchProducts() {
        when(productRepository.findByNameContainingIgnoreCase("test"))
                .thenReturn(Arrays.asList(product));

        List<Product> products = productService.searchProducts("test");

        assertThat(products).isNotEmpty();
        assertThat(products.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository, times(1))
                .findByNameContainingIgnoreCase("test");
    }

    @Test
    void testSaveProductSuccessfully() throws IOException {
        when(multipartFile.getOriginalFilename()).thenReturn("image.png");
        when(multipartFile.getBytes()).thenReturn("dummy".getBytes());

        productService.saveProduct(product, multipartFile);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(captor.capture());

        Product savedProduct = captor.getValue();
        assertThat(savedProduct.getImageName()).isNotNull();
        assertThat(savedProduct.getImageName()).endsWith(".png");
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<Product> products = productService.getAllProducts();

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductByIdFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Product result = productService.getProductById(2L);

        assertThat(result).isNull();
        verify(productRepository, times(1)).findById(2L);
    }
}
