package com.laptopstore.business.service;

import com.laptopstore.data.entity.Wishlist;

public interface WishlistService {
    
    Wishlist getWishlistByUserId(Long userId);
    
    Wishlist addItemToWishlist(Long userId, Long productId);
    
    Wishlist removeItemFromWishlist(Long userId, Long productId);
    
    void clearWishlist(Long userId);
    
    boolean isProductInWishlist(Long userId, Long productId);
}
