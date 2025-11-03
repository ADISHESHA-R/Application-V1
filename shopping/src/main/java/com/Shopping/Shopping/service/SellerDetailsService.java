package com.Shopping.Shopping.service;

import com.Shopping.Shopping.model.Seller;
import com.Shopping.Shopping.repository.SellerRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class SellerDetailsService implements UserDetailsService {

    private final SellerRepository sellerRepository;

    public SellerDetailsService(SellerRepository repo) {
        this.sellerRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Seller seller = sellerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(seller.getUsername())
                .password(seller.getPassword())
                .roles("SELLER")
                .build();
    }
}
