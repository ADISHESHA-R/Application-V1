package com.Shopping.Shopping.controller;

import com.Shopping.Shopping.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String viewHomePage(Model model, jakarta.servlet.http.HttpSession session) {
        logger.info("=== HOME PAGE REQUEST STARTED ===");
        try {
            // Check if user is authenticated
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isUser = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
            
            logger.info("Authentication status - isUser: {}, username: {}", isUser, 
                       auth != null ? auth.getName() : "null");
            
            logger.info("Fetching all products...");
            var products = productService.getAllProducts();
            logger.info("Successfully fetched {} products", products.size());
            
            // Calculate cart count
            @SuppressWarnings("unchecked")
            java.util.List<com.Shopping.Shopping.model.CartItem> cart = 
                (java.util.List<com.Shopping.Shopping.model.CartItem>) session.getAttribute("cart");
            int cartCount = 0;
            if (cart != null) {
                cartCount = cart.stream().mapToInt(com.Shopping.Shopping.model.CartItem::getQuantity).sum();
            }
            
            model.addAttribute("products", products);
            model.addAttribute("isUser", isUser);
            model.addAttribute("cartCount", cartCount);
            
            // Get all categories for filtering
            List<String> categories = productService.getAllCategories();
            model.addAttribute("categories", categories);
            
            // Check for cart success/error messages
            String cartSuccess = (String) session.getAttribute("cartSuccessMessage");
            String cartError = (String) session.getAttribute("cartErrorMessage");
            if (cartSuccess != null) {
                model.addAttribute("cartSuccess", cartSuccess);
                session.removeAttribute("cartSuccessMessage");
            }
            if (cartError != null) {
                model.addAttribute("cartError", cartError);
                session.removeAttribute("cartErrorMessage");
            }
            
            logger.info("=== HOME PAGE REQUEST COMPLETED SUCCESSFULLY ===");
            return "index";
        } catch (Exception e) {
            logger.error("=== ERROR IN HOME PAGE REQUEST ===", e);
            throw e;
        }
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model, jakarta.servlet.http.HttpSession session) {
        logger.info("=== SEARCH REQUEST STARTED ===");
        logger.info("Search keyword: '{}'", keyword);
        
        try {
            // Check if user is authenticated
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isUser = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
            
            logger.info("Authentication status - isUser: {}, username: {}", isUser, 
                       auth != null ? auth.getName() : "null");
            
            logger.info("Performing search for keyword: '{}'", keyword);
            var products = productService.searchProducts(keyword);
            logger.info("Search completed. Found {} products for keyword: '{}'", products.size(), keyword);
            
            // Calculate cart count
            @SuppressWarnings("unchecked")
            java.util.List<com.Shopping.Shopping.model.CartItem> cart = 
                (java.util.List<com.Shopping.Shopping.model.CartItem>) session.getAttribute("cart");
            int cartCount = 0;
            if (cart != null) {
                cartCount = cart.stream().mapToInt(com.Shopping.Shopping.model.CartItem::getQuantity).sum();
            }
            
            model.addAttribute("products", products);
            model.addAttribute("isUser", isUser);
            model.addAttribute("cartCount", cartCount);
            model.addAttribute("searchKeyword", keyword);
            
            // Get all categories for filtering
            List<String> categories = productService.getAllCategories();
            model.addAttribute("categories", categories);
            
            logger.info("=== SEARCH REQUEST COMPLETED SUCCESSFULLY ===");
            return "index";
        } catch (Exception e) {
            logger.error("=== ERROR IN SEARCH REQUEST for keyword: '{}' ===", keyword, e);
            throw e;
        }
    }

    @GetMapping("/category/{categoryName}")
    public String getProductsByCategory(@PathVariable("categoryName") String categoryName, Model model, jakarta.servlet.http.HttpSession session) {
        logger.info("=== CATEGORY FILTER REQUEST STARTED ===");
        logger.info("Category: '{}'", categoryName);
        
        try {
            // Check if user is authenticated
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isUser = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
            
            logger.info("Authentication status - isUser: {}, username: {}", isUser, 
                       auth != null ? auth.getName() : "null");
            
            logger.info("Fetching products for category: '{}'", categoryName);
            var products = productService.getProductsByCategory(categoryName);
            logger.info("Found {} products in category: '{}'", products.size(), categoryName);
            
            // Calculate cart count
            @SuppressWarnings("unchecked")
            java.util.List<com.Shopping.Shopping.model.CartItem> cart = 
                (java.util.List<com.Shopping.Shopping.model.CartItem>) session.getAttribute("cart");
            int cartCount = 0;
            if (cart != null) {
                cartCount = cart.stream().mapToInt(com.Shopping.Shopping.model.CartItem::getQuantity).sum();
            }
            
            model.addAttribute("products", products);
            model.addAttribute("isUser", isUser);
            model.addAttribute("cartCount", cartCount);
            model.addAttribute("selectedCategory", categoryName);
            
            // Get all categories for filtering
            List<String> categories = productService.getAllCategories();
            model.addAttribute("categories", categories);
            
            logger.info("=== CATEGORY FILTER REQUEST COMPLETED SUCCESSFULLY ===");
            return "index";
        } catch (Exception e) {
            logger.error("=== ERROR IN CATEGORY FILTER REQUEST ===", e);
            throw e;
        }
    }

    @GetMapping("/help-center")
    public String helpCenter() {
        return "help-center";
    }

    @GetMapping("/contact-us")
    public String contactUs() {
        return "contact-us";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "terms-of-service";
    }
}
