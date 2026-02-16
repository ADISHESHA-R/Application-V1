package com.Shopping.Shopping.controller.api;

import com.Shopping.Shopping.dto.ApiResponse;
import com.Shopping.Shopping.dto.ProductDTO;
import com.Shopping.Shopping.dto.UserDTO;
import com.Shopping.Shopping.model.Product;
import com.Shopping.Shopping.model.User;
import com.Shopping.Shopping.repository.UserRepository;
import com.Shopping.Shopping.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"}, allowCredentials = "true")
public class ApiUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;

    public ApiUserController(UserRepository userRepository, PasswordEncoder passwordEncoder, ProductService productService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDTO>> signup(@ModelAttribute UserSignupRequest request) {
        try {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Username already exists"));
            }

            if (!isValidPassword(request.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Password must be at least 8 characters long and include an uppercase letter, lowercase letter, digit, and special character."));
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAlternateNumber(request.getAlternateNumber());
            user.setAddress(request.getAddress());

            if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
                user.setPhoto(request.getPhoto().getBytes());
            }

            User savedUser = userRepository.saveAndFlush(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", convertToDTO(savedUser)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Not authenticated"));
        }

        Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found"));
        }

        return ResponseEntity.ok(ApiResponse.success(convertToDTO(userOpt.get())));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute UserUpdateRequest request) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
            }

            User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

            user.setAlternateNumber(request.getAlternateNumber());
            user.setAddress(request.getAddress());

            if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
                user.setPhoto(request.getPhoto().getBytes());
            }

            User updatedUser = userRepository.saveAndFlush(user);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", convertToDTO(updatedUser)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHome(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Not authenticated"));
        }

        Optional<User> userOpt = userRepository.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found"));
        }

        // Get user data
        UserDTO userDTO = convertToDTO(userOpt.get());
        
        // Get all products for home page
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDTOs = products.stream()
            .map(this::convertProductToDTO)
            .collect(Collectors.toList());
        
        // Get all categories
        List<String> categories = productService.getAllCategories();
        
        // Build home page response
        Map<String, Object> homeData = new HashMap<>();
        homeData.put("user", userDTO);
        homeData.put("products", productDTOs);
        homeData.put("categories", categories);
        
        return ResponseEntity.ok(ApiResponse.success("Home data retrieved successfully", homeData));
    }
    
    private ProductDTO convertProductToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setUniqueProductId(product.getUniqueProductId());
        dto.setImageUrl("/product-image/" + product.getId());
        return dto;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAlternateNumber(user.getAlternateNumber());
        dto.setAddress(user.getAddress());
        dto.setPhotoBase64(user.getPhotoBase64());
        return dto;
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
            password.matches(".*[A-Z].*") &&
            password.matches(".*[a-z].*") &&
            password.matches(".*\\d.*") &&
            password.matches(".*[!@#$%^&*()_+=<>?].*");
    }

    @lombok.Data
    static class UserSignupRequest {
        private String username;
        private String password;
        private String phoneNumber;
        private String alternateNumber;
        private String address;
        private MultipartFile photo;
    }

    @lombok.Data
    static class UserUpdateRequest {
        private String alternateNumber;
        private String address;
        private MultipartFile photo;
    }
}
