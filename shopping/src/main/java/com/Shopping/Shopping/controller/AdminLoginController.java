package com.Shopping.Shopping.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminLoginController {

    @GetMapping("/admin-login")
    public String adminLoginPage(Model model) {
        return "admin-login";
    }
}
