package com.laptopstore.application.facade;

import com.laptopstore.application.dto.order.CartDTO;
import com.laptopstore.application.mapper.CartMapper;
import com.laptopstore.application.security.services.UserDetailsImpl;
import com.laptopstore.business.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final CartMapper cartMapper;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    public CartDTO getCurrentUserCart() {
        Long userId = getCurrentUserId();
        return cartMapper.toCartDTO(cartService.getCartByUserId(userId));
    }

    public CartDTO addItemToCart(Long productId, Integer quantity) {
        Long userId = getCurrentUserId();
        return cartMapper.toCartDTO(cartService.addItemToCart(userId, productId, quantity));
    }

    public CartDTO updateCartItemQuantity(Long cartItemId, Integer quantity) {
        Long userId = getCurrentUserId();
        return cartMapper.toCartDTO(cartService.updateCartItemQuantity(userId, cartItemId, quantity));
    }

    public CartDTO removeCartItem(Long cartItemId) {
        Long userId = getCurrentUserId();
        return cartMapper.toCartDTO(cartService.removeCartItem(userId, cartItemId));
    }

    public void clearCart() {
        Long userId = getCurrentUserId();
        cartService.clearCart(userId);
    }
}
