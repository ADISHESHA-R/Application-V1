package com.Shopping.Shopping.service;

import com.Shopping.Shopping.model.CartItem;
import com.Shopping.Shopping.model.Product;
import com.Shopping.Shopping.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private static final String CART_SESSION_KEY = "cart";
    
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void addProductToCart(Long productId, int quantity, HttpSession session) {
        log.info("Adding product {} to cart with quantity {}", productId, quantity);
        List<CartItem> cart = getCart(session);
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            boolean found = false;
            for (CartItem item : cart) {
                if (item.getProduct().getId().equals(productId)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cart.add(new CartItem(product, quantity));
            }
            session.setAttribute(CART_SESSION_KEY, cart);
            log.debug("Product {} added to cart successfully", productId);
        } else {
            log.warn("Product {} not found", productId);
        }
    }

    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void removeProductFromCart(Long productId, HttpSession session) {
        log.info("Removing product {} from cart", productId);
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProduct().getId().equals(productId));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        log.info("Clearing cart");
        session.setAttribute(CART_SESSION_KEY, new ArrayList<CartItem>());
    }

    public void updateProductQuantity(Long productId, int quantity, HttpSession session) {
        log.info("Updating product {} quantity to {}", productId, quantity);
        List<CartItem> cart = getCart(session);
        for (CartItem item : cart) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        session.setAttribute(CART_SESSION_KEY, cart);
    }
}
