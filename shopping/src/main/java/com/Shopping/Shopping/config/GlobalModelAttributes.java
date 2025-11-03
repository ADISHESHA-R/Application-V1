package com.Shopping.Shopping.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import com.Shopping.Shopping.model.CartItem;
import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = false;
        boolean isUser = false;
        boolean isSeller = false;

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() != null
                && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            isAuthenticated = true;
            isUser = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_USER".equals(a.getAuthority()));
            isSeller = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_SELLER".equals(a.getAuthority()));
        }

        // Calculate cart count
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        int cartCount = 0;
        if (cart != null) {
            cartCount = cart.stream().mapToInt(CartItem::getQuantity).sum();
        }

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isUser", isUser);
        model.addAttribute("isSeller", isSeller);
        model.addAttribute("cartCount", cartCount);
    }
}


