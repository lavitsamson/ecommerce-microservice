package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public Cart addItem(@RequestParam String userId,
                        @RequestParam String skuCode,
                        @RequestParam int quantity) {
        return cartService.addItemToCart(userId, skuCode, quantity);
    }

    @GetMapping
    public Cart getCart(@RequestParam String userId) {
        return cartService.getCart(userId);
    }

    @DeleteMapping("/remove")
    public void removeItem(@RequestParam String userId,
                           @RequestParam String skuCode) {
        cartService.removeItemFromCart(userId, skuCode);
    }
}
