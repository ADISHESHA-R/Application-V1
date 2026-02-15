package com.Shopping.Shopping.controller;

import com.Shopping.Shopping.model.CartItem;
import com.Shopping.Shopping.model.Product;
import com.Shopping.Shopping.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private ProductService productService;
    
    @Value("${razorpay.key}")
    private String razorpayKey;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        logger.info("=== CART VIEW REQUEST STARTED ===");
        logger.info("Session ID: {}", session.getId());
        
        try {
            @SuppressWarnings("unchecked")
            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cart");
            if (cartItems == null) {
                cartItems = new ArrayList<>();
                logger.info("No existing cart found, created new empty cart");
            } else {
                logger.info("Found existing cart with {} items", cartItems.size());
            }

            // Check if cart is empty and redirect to home page
            if (cartItems.isEmpty()) {
                logger.info("Cart is empty, redirecting to home page");
                return "redirect:/";
            }

            double total = cartItems.stream()
                    .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                    .sum();

            logger.info("Cart total calculated: {}", total);

            // Check if user is authenticated
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isUser = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
            logger.info("Authentication status - isUser: {}, username: {}", isUser, 
                       auth != null ? auth.getName() : "null");

            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", total);
            model.addAttribute("key", razorpayKey);
            model.addAttribute("isUser", isUser);

            logger.info("=== CART VIEW REQUEST COMPLETED SUCCESSFULLY ===");
            return "checkout";
        } catch (Exception e) {
            logger.error("=== ERROR IN CART VIEW REQUEST ===", e);
            throw e;
        }
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @RequestParam(name = "quantity", required = false, defaultValue = "1") int quantity,
                            HttpSession session) {
        logger.info("=== ADD TO CART REQUEST STARTED ===");
        logger.info("Product ID: {}, Quantity: {}, Session ID: {}", id, quantity, session.getId());
        
        try {
            logger.info("Fetching product details for ID: {}", id);
            Product product = productService.getProductById(id);
            
            if (product == null) {
                logger.error("Product not found for ID: {}", id);
                logger.info("=== ADD TO CART REQUEST FAILED - PRODUCT NOT FOUND ===");
                return "redirect:/";
            }
            
            logger.info("Product found - Name: '{}', Price: {}", product.getName(), product.getPrice());
            
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                logger.info("Created new cart");
            } else {
                logger.info("Found existing cart with {} items", cart.size());
            }

            boolean found = false;
            for (CartItem item : cart) {
                if (item.getProduct().getId().equals(id)) {
                    int oldQuantity = item.getQuantity();
                    item.setQuantity(item.getQuantity() + quantity);
                    logger.info("Updated existing cart item - Product: '{}', Old Qty: {}, New Qty: {}", 
                               product.getName(), oldQuantity, item.getQuantity());
                    found = true;
                    break;
                }
            }

            if (!found) {
                cart.add(new CartItem(product, quantity));
                logger.info("Added new item to cart - Product: '{}', Quantity: {}", product.getName(), quantity);
            }

            session.setAttribute("cart", cart);
            logger.info("Cart updated successfully. Total items in cart: {}", cart.size());
            
            // Calculate total items for feedback
            int totalItems = cart.stream().mapToInt(CartItem::getQuantity).sum();
            logger.info("Total quantity of items in cart: {}", totalItems);
            
            // Set success message in session
            session.setAttribute("cartSuccessMessage", "Item added to cart successfully!");
            
            logger.info("=== ADD TO CART REQUEST COMPLETED SUCCESSFULLY ===");
            return "redirect:/?cartAdded=true";
        } catch (Exception e) {
            logger.error("=== ERROR IN ADD TO CART REQUEST for Product ID: {} ===", id, e);
            logger.error("Exception details: {}", e.getMessage(), e);
            session.setAttribute("cartErrorMessage", "Failed to add item to cart. Please try again.");
            return "redirect:/?cartError=true";
        }
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        logger.info("=== REMOVE FROM CART REQUEST STARTED ===");
        logger.info("Product ID: {}, Session ID: {}", id, session.getId());
        
        try {
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                int initialSize = cart.size();
                cart.removeIf(item -> item.getProduct().getId().equals(id));
                int finalSize = cart.size();
                
                if (initialSize > finalSize) {
                    logger.info("Successfully removed product ID: {} from cart. Items before: {}, Items after: {}", 
                               id, initialSize, finalSize);
                } else {
                    logger.warn("Product ID: {} not found in cart for removal", id);
                }
                
                session.setAttribute("cart", cart);
                
                // Check if cart is now empty and redirect to home page
                if (cart.isEmpty()) {
                    logger.info("Cart is now empty after removal, redirecting to home page");
                    return "redirect:/";
                }
            } else {
                logger.warn("No cart found in session for removal");
            }
            
            logger.info("=== REMOVE FROM CART REQUEST COMPLETED SUCCESSFULLY ===");
            return "redirect:/cart";
        } catch (Exception e) {
            logger.error("=== ERROR IN REMOVE FROM CART REQUEST for Product ID: {} ===", id, e);
            throw e;
        }
    }

    @PostMapping("/cart/update/{id}")
    public String updateQuantity(@PathVariable Long id,
                                 @RequestParam int quantity,
                                 HttpSession session) {
        logger.info("=== UPDATE QUANTITY REQUEST STARTED ===");
        logger.info("Product ID: {}, New Quantity: {}, Session ID: {}", id, quantity, session.getId());
        
        try {
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart != null) {
                boolean found = false;
                for (CartItem item : cart) {
                    if (item.getProduct().getId().equals(id)) {
                        int oldQuantity = item.getQuantity();
                        
                        if (quantity <= 0) {
                            // Remove item if quantity is 0 or negative
                            cart.remove(item);
                            logger.info("Removed product ID: {} from cart due to zero quantity", id);
                        } else {
                            item.setQuantity(quantity);
                            logger.info("Updated quantity for product ID: {} from {} to {}", 
                                       id, oldQuantity, quantity);
                        }
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    logger.warn("Product ID: {} not found in cart for quantity update", id);
                }
                
                session.setAttribute("cart", cart);
                
                // Check if cart is now empty and redirect to home page
                if (cart.isEmpty()) {
                    logger.info("Cart is now empty after quantity update, redirecting to home page");
                    return "redirect:/";
                }
            } else {
                logger.warn("No cart found in session for quantity update");
            }
            
            logger.info("=== UPDATE QUANTITY REQUEST COMPLETED SUCCESSFULLY ===");
            return "redirect:/cart";
        } catch (Exception e) {
            logger.error("=== ERROR IN UPDATE QUANTITY REQUEST for Product ID: {} ===", id, e);
            throw e;
        }
    }

}
