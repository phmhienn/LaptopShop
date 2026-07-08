package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.ProductService;
import com.laptopstore.business.service.UserService;
import com.laptopstore.business.service.WishlistService;
import com.laptopstore.data.entity.Product;
import com.laptopstore.data.entity.User;
import com.laptopstore.data.entity.Wishlist;
import com.laptopstore.data.entity.WishlistItem;
import com.laptopstore.data.repository.WishlistItemRepository;
import com.laptopstore.data.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserService userService;
    private final ProductService productService;

    @Override
    @Transactional
    public Wishlist getWishlistByUserId(Long userId) {
        return wishlistRepository.findFirstByUserId(userId).orElseGet(() -> {
            User user = userService.getUserById(userId);
            Wishlist newWishlist = new Wishlist();
            newWishlist.setUser(user);
            return wishlistRepository.save(newWishlist);
        });
    }

    @Override
    @Transactional
    public Wishlist addItemToWishlist(Long userId, Long productId) {
        Wishlist wishlist = getWishlistByUserId(userId);
        
        if (wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId)) {
            throw new ValidationException("Product is already in your wishlist");
        }
        
        Product product = productService.getProductById(productId);
        
        WishlistItem newItem = new WishlistItem();
        newItem.setWishlist(wishlist);
        newItem.setProduct(product);
        
        wishlist.getItems().add(newItem);
        
        return wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public Wishlist removeItemFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = getWishlistByUserId(userId);
        
        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getId(), productId);
        wishlist.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        
        return wishlist;
    }

    @Override
    @Transactional
    public void clearWishlist(Long userId) {
        Wishlist wishlist = getWishlistByUserId(userId);
        wishlist.getItems().clear();
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long userId, Long productId) {
        Wishlist wishlist = getWishlistByUserId(userId);
        return wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), productId);
    }
}
