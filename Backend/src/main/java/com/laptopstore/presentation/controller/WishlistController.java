package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.product.ProductDTO;
import com.laptopstore.application.facade.WishlistFacade;
import com.laptopstore.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistFacade wishlistFacade;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getWishlist() {
        List<ProductDTO> wishlist = wishlistFacade.getUserWishlist();
        return ResponseEntity.ok(ApiResponse.success(wishlist));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> addToWishlist(@PathVariable Long productId) {
        wishlistFacade.addItemToWishlist(productId);
        return ResponseEntity.ok(ApiResponse.success("Product added to wishlist"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(@PathVariable Long productId) {
        wishlistFacade.removeItemFromWishlist(productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearWishlist() {
        wishlistFacade.clearWishlist();
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared"));
    }
    
    @GetMapping("/{productId}/check")
    public ResponseEntity<ApiResponse<Boolean>> checkProductInWishlist(@PathVariable Long productId) {
        boolean isInWishlist = wishlistFacade.isProductInWishlist(productId);
        return ResponseEntity.ok(ApiResponse.success(isInWishlist));
    }
}
