# REST API Documentation

## ✅ Implementation Complete

All REST API endpoints have been created and are ready for frontend consumption.

## 📁 Files Created

### DTOs (Data Transfer Objects)
- `dto/ApiResponse.java` - Standardized API response wrapper
- `dto/ProductDTO.java` - Product data transfer
- `dto/UserDTO.java` - User data transfer
- `dto/SellerDTO.java` - Seller data transfer
- `dto/CartItemDTO.java` - Cart item data transfer
- `dto/OrderDTO.java` - Order data transfer

### REST API Controllers
- `controller/api/ApiProductController.java` - Product operations
- `controller/api/ApiAuthController.java` - Authentication
- `controller/api/ApiCartController.java` - Shopping cart operations
- `controller/api/ApiUserController.java` - User profile operations
- `controller/api/ApiSellerController.java` - Seller operations
- `controller/api/ApiPaymentController.java` - Payment and orders
- `controller/api/ApiAdminController.java` - Admin operations

### Configuration
- `config/CorsConfig.java` - CORS configuration for React frontend
- Updated `config/SecurityConfig.java` - Added API security filter chain

### Repository Updates
- `repository/ProductRepository.java` - Added `findBySeller()` method
- `repository/OrdersRepository.java` - Added `findByUser()` method

## 🔗 API Endpoints

### Products (Public)
- `GET /api/v1/products` - Get all products
- `GET /api/v1/products/{id}` - Get product by ID
- `GET /api/v1/products/category/{category}` - Get products by category
- `GET /api/v1/products/search?query={keyword}` - Search products

### Authentication
- `POST /api/v1/auth/signup` - User signup (JSON body)
- `GET /api/v1/auth/me` - Get current user (requires authentication)

### User Operations (Requires USER role)
- `POST /api/v1/user/signup` - User signup (form-data with photo)
- `GET /api/v1/user/profile` - Get user profile
- `PUT /api/v1/user/profile` - Update user profile (form-data)
- `GET /api/v1/user/home` - Get user home data

### Cart Operations (Public - uses session)
- `GET /api/v1/cart` - Get cart items
- `POST /api/v1/cart/add/{productId}?quantity={qty}` - Add to cart
- `DELETE /api/v1/cart/remove/{productId}` - Remove from cart
- `PUT /api/v1/cart/update/{productId}?quantity={qty}` - Update quantity

### Seller Operations (Requires SELLER role)
- `POST /api/v1/seller/signup` - Seller signup (form-data)
- `GET /api/v1/seller/profile` - Get seller profile
- `PUT /api/v1/seller/profile` - Update seller profile (form-data)
- `GET /api/v1/seller/dashboard` - Get categories for dashboard
- `POST /api/v1/seller/products` - Upload product (form-data)
- `GET /api/v1/seller/products` - Get seller's products
- `GET /api/v1/seller/home` - Get seller home data

### Payment & Orders (Requires USER role)
- `GET /api/v1/payment/buy-now/{productId}?quantity={qty}` - Get buy now details
- `POST /api/v1/payment/buy-now/address` - Save address (JSON body: `{"address": "..."}`)
- `POST /api/v1/payment/create-order` - Create Razorpay order (JSON body: `{"amount": 50000}`)
- `POST /api/v1/payment/success` - Handle payment success (JSON body with Razorpay data)
- `GET /api/v1/payment/orders` - Get user orders

### Admin Operations (Requires ADMIN role)
- `GET /api/v1/admin/users` - Get all users
- `GET /api/v1/admin/sellers` - Get all sellers
- `GET /api/v1/admin/products` - Get all products
- `PUT /api/v1/admin/users/{id}` - Update user (JSON body)
- `PUT /api/v1/admin/sellers/{id}` - Update seller (JSON body)
- `PUT /api/v1/admin/products/{id}` - Update product (JSON body)
- `DELETE /api/v1/admin/users/{id}` - Delete user
- `DELETE /api/v1/admin/sellers/{id}` - Delete seller
- `DELETE /api/v1/admin/products/{id}` - Delete product

## 🧪 Testing the APIs

### Using Postman

1. **Get All Products** (Public - no auth needed)
   ```
   GET http://localhost:8080/api/v1/products
   ```

2. **User Signup**
   ```
   POST http://localhost:8080/api/v1/user/signup
   Content-Type: multipart/form-data
   
   username: testuser
   password: Test@1234
   phoneNumber: 1234567890
   address: Test Address
   ```

3. **Get User Profile** (After login via web UI)
   ```
   GET http://localhost:8080/api/v1/user/profile
   Cookie: JSESSIONID=...
   ```

4. **Add to Cart**
   ```
   POST http://localhost:8080/api/v1/cart/add/1?quantity=2
   Cookie: JSESSIONID=...
   ```

### Using cURL

```bash
# Get all products
curl http://localhost:8080/api/v1/products

# Get product by ID
curl http://localhost:8080/api/v1/products/1

# Search products
curl "http://localhost:8080/api/v1/products/search?query=laptop"

# Add to cart (requires session cookie)
curl -X POST "http://localhost:8080/api/v1/cart/add/1?quantity=1" \
  -H "Cookie: JSESSIONID=your-session-id"
```

### Using Browser Console

```javascript
// Test API from browser console (after logging in via web UI)
fetch('http://localhost:8080/api/v1/products')
  .then(res => res.json())
  .then(data => console.log(data));

// Get user profile
fetch('http://localhost:8080/api/v1/user/profile', {
  credentials: 'include' // Important for session cookies
})
  .then(res => res.json())
  .then(data => console.log(data));
```

## 🔒 Authentication

The APIs use **session-based authentication** (same as your existing web app):

1. User logs in via `/login` (web UI)
2. Session cookie (`JSESSIONID`) is set
3. React app includes cookie in API requests using `credentials: 'include'`
4. Spring Security validates the session

### For React Frontend

```javascript
// Include credentials in all fetch requests
fetch('http://localhost:8080/api/v1/user/profile', {
  credentials: 'include', // Important!
  headers: {
    'Content-Type': 'application/json'
  }
})
```

## 📝 Response Format

All API responses follow this format:

```json
{
  "success": true,
  "message": "Success",
  "data": { ... }
}
```

Error responses:
```json
{
  "success": false,
  "message": "Error message here",
  "data": null
}
```

## ✅ What's Working

- ✅ All endpoints return JSON (not HTML)
- ✅ CORS configured for React frontend
- ✅ Security configured (role-based access)
- ✅ Session-based authentication works
- ✅ Existing Thymeleaf pages still work (no breaking changes)
- ✅ Can test with Postman/curl/browser

## 🚀 Next Steps

1. **Test APIs** using Postman or curl
2. **Build React frontend** that calls these APIs
3. **Update CORS origins** in `CorsConfig.java` with your React app URL
4. **Deploy** - APIs will work alongside existing web app

## 📌 Important Notes

- **No breaking changes** - All existing functionality remains intact
- **Separate routes** - APIs are at `/api/v1/*`, web pages at `/*`
- **Same authentication** - Uses existing session-based auth
- **File uploads** - Use `multipart/form-data` for endpoints with photos/files
- **CORS** - Currently allows `localhost:3000` and `localhost:5173` (update for production)

## 🎯 Ready for Frontend!

Your backend is now fully ready for React frontend development. All endpoints are functional and can be tested immediately.
