package com.laptopstore.application.facade;

import com.laptopstore.application.dto.product.ProductDTO;
import com.laptopstore.application.mapper.ProductMapper;
import com.laptopstore.application.security.services.UserDetailsImpl;
import com.laptopstore.business.service.WishlistService;
import com.laptopstore.data.entity.Wishlist;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WishlistFacade {

    private final WishlistService wishlistService;
    private final ProductMapper productMapper;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ProductDTO> getUserWishlist() {
        Long userId = getCurrentUserId();
        Wishlist wishlist = wishlistService.getWishlistByUserId(userId);
        
        return wishlist.getItems().stream()
                // Using basic DTO without dynamic review stats to save DB calls, can be optimized later
                .map(item -> productMapper.toProductDTO(item.getProduct(), 0.0, 0L)) 
                .collect(Collectors.toList());
    }

    public void addItemToWishlist(Long productId) {
        Long userId = getCurrentUserId();
        wishlistService.addItemToWishlist(userId, productId);
    }

    public void removeItemFromWishlist(Long productId) {
        Long userId = getCurrentUserId();
        wishlistService.removeItemFromWishlist(userId, productId);
    }

    public void clearWishlist() {
        Long userId = getCurrentUserId();
        wishlistService.clearWishlist(userId);
    }
    
    public boolean isProductInWishlist(Long productId) {
        Long userId = getCurrentUserId();
        return wishlistService.isProductInWishlist(userId, productId);
    }
}
