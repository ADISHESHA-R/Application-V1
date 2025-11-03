package com.Shopping.Shopping.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.Shopping.Shopping.model.Orders;
import com.Shopping.Shopping.model.Product;
import com.Shopping.Shopping.model.User;
import com.Shopping.Shopping.repository.OrdersRepository;
import com.Shopping.Shopping.repository.ProductRepository;
import com.Shopping.Shopping.repository.UserRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    
    private final String razorpayKey;
    private final String razorpaySecret;
    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public PaymentController(@Value("${razorpay.key}") String razorpayKey,
                             @Value("${razorpay.secret}") String razorpaySecret,
                             OrdersRepository ordersRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository) {
        this.razorpayKey = razorpayKey;
        this.razorpaySecret = razorpaySecret;
        this.ordersRepository = ordersRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // 🟢 Buy Now - not used in cart flow
    @PostMapping("/buy-now/{productId}")
    public String buyNow(@PathVariable Long productId,
                         @RequestParam(defaultValue = "1") int quantity,
                         Principal principal,
                         Model model) throws RazorpayException {
        log.info("=== BUY NOW REQUEST STARTED ===");
        log.info("Product ID: {}, Quantity: {}", productId, quantity);
        
        try {
            if (principal == null) {
                log.warn("Buy now attempt without authentication");
                log.info("=== BUY NOW REQUEST FAILED - NOT AUTHENTICATED ===");
                return "redirect:/login";
            }

            log.info("User authenticated: {}", principal.getName());
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Fetching product details for ID: {}", productId);
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            log.info("Product found - Name: '{}', Price: {}", product.getName(), product.getPrice());
            int amountInPaise = (int) (product.getPrice() * quantity * 100);
            log.info("Calculated amount: {} paise (₹{})", amountInPaise, amountInPaise / 100);

            log.info("Creating Razorpay order...");
            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);
            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = client.orders.create(options);
            log.info("Razorpay order created successfully - Order ID: {}", order.get("id").toString());

            model.addAttribute("key", razorpayKey);
            model.addAttribute("amount", amountInPaise / 100);
            model.addAttribute("orderId", order.get("id"));
            model.addAttribute("product", product);
            model.addAttribute("quantity", quantity);

            log.info("=== BUY NOW REQUEST COMPLETED SUCCESSFULLY ===");
            return "razorpay_checkout";
        } catch (Exception e) {
            log.error("=== ERROR IN BUY NOW REQUEST for Product ID: {} ===", productId, e);
            throw e;
        }
    }

    // 🟢 AJAX: Create Razorpay Order from Total Amount
    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
        log.info("=== CREATE ORDER REQUEST STARTED ===");
        log.info("Request data: {}", data);
        
        try {
            int amount = (int) data.get("amount");
            log.info("Creating order for amount: {} paise (₹{})", amount, amount / 100);

            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);
            JSONObject options = new JSONObject();
            options.put("amount", amount);
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = client.orders.create(options);
            log.info("Razorpay order created successfully - Order ID: {}", order.get("id").toString());

            Map<String, Object> response = new HashMap<>();
            response.put("id", order.get("id"));
            response.put("amount", order.get("amount"));
            
            log.info("=== CREATE ORDER REQUEST COMPLETED SUCCESSFULLY ===");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("=== ERROR IN CREATE ORDER REQUEST ===", e);
            throw e;
        }
    }

    // 🟢 Handle Payment Success
    @PostMapping("/payment-success")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> handlePayment(@RequestBody Map<String, String> data, Principal principal) {
        log.info("=== PAYMENT SUCCESS REQUEST STARTED ===");
        log.info("Payment data: {}", data);
        
        try {
            if (principal == null) {
                log.warn("Payment attempt without authentication");
                log.info("=== PAYMENT SUCCESS REQUEST FAILED - NOT AUTHENTICATED ===");
                return ResponseEntity.status(401).build();
            }

            log.info("Processing payment for user: {}", principal.getName());
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Creating order record...");
            Orders order = new Orders();
            order.setRazorpayPaymentId(data.get("razorpay_payment_id"));
            order.setRazorpayOrderId(data.get("razorpay_order_id"));
            order.setRazorpaySignature(data.get("razorpay_signature"));
            order.setAmount(500); // optional: make dynamic if needed
            order.setOrderDate(LocalDateTime.now());
            order.setUser(user);
            order.setEmail(user.getUsername());

            ordersRepository.save(order);
            log.info("Order saved successfully for user: {}, Order ID: {}, Payment ID: {}", 
                    principal.getName(), order.getRazorpayOrderId(), order.getRazorpayPaymentId());
            
            log.info("=== PAYMENT SUCCESS REQUEST COMPLETED SUCCESSFULLY ===");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("=== ERROR IN PAYMENT SUCCESS REQUEST ===", e);
            throw e;
        }
    }

    // 🟢 Payment Success Page
    @GetMapping("/payment-success-page")
    public String paymentSuccessPage() {
        log.info("=== PAYMENT SUCCESS PAGE REQUEST STARTED ===");
        try {
            log.info("Rendering payment success page");
            log.info("=== PAYMENT SUCCESS PAGE REQUEST COMPLETED SUCCESSFULLY ===");
            return "payment_success";
        } catch (Exception e) {
            log.error("=== ERROR IN PAYMENT SUCCESS PAGE REQUEST ===", e);
            throw e;
        }
    }
}
