package com.laptopstore.business.service;

import com.laptopstore.data.entity.Cart;

public interface CartService {
    
    Cart getCartByUserId(Long userId);
    
    Cart addItemToCart(Long userId, Long productId, Integer quantity);
    
    Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity);
    
    Cart removeCartItem(Long userId, Long cartItemId);
    
    void clearCart(Long userId);
}
