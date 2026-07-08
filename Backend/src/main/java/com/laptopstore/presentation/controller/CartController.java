package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.order.CartDTO;
import com.laptopstore.application.dto.order.CartItemRequestDTO;
import com.laptopstore.application.facade.CartFacade;
import com.laptopstore.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartFacade cartFacade;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart() {
        CartDTO cartDTO = cartFacade.getCurrentUserCart();
        return ResponseEntity.ok(ApiResponse.success(cartDTO));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addItemToCart(@Valid @RequestBody CartItemRequestDTO request) {
        CartDTO cartDTO = cartFacade.addItemToCart(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cartDTO));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItemQuantity(
            @PathVariable Long itemId,
            @RequestParam("quantity") Integer quantity) {
        CartDTO cartDTO = cartFacade.updateCartItemQuantity(itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated", cartDTO));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeCartItem(@PathVariable Long itemId) {
        CartDTO cartDTO = cartFacade.removeCartItem(itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cartDTO));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        cartFacade.clearCart();
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
    }
}
