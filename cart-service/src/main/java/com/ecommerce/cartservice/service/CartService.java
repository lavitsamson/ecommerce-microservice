package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.client.ProductServiceClient;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;

    public Cart addItemToCart(String userId, String skuCode, int quantity) {
        // Check if product exists
        boolean productExists = productServiceClient.isProductValid(skuCode);

        if (!productExists) {
            throw new RuntimeException("Product with SKU: " + skuCode + " not found!");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(Cart.builder()
                        .userId(userId)
                        .build());

        CartItem item = CartItem.builder()
                .skuCode(skuCode)
                .quantity(quantity)
                .cart(cart)
                .build();

        cart.getItems().add(item);
        return cartRepository.save(cart);
    }

    // getCart and removeItemFromCart remain the same
}
