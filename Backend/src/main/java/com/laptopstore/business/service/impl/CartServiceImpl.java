package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.CartService;
import com.laptopstore.business.service.ProductService;
import com.laptopstore.business.service.UserService;
import com.laptopstore.data.entity.Cart;
import com.laptopstore.data.entity.CartItem;
import com.laptopstore.data.entity.Product;
import com.laptopstore.data.entity.User;
import com.laptopstore.data.repository.CartItemRepository;
import com.laptopstore.data.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    @Override
    @Transactional
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserIdWithItems(userId).orElseGet(() -> {
            User user = userService.getUserById(userId);
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional
    public Cart addItemToCart(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than 0");
        }

        Cart cart = getCartByUserId(userId);
        Product product = productService.getProductById(productId);

        if (product.getStock() < quantity) {
            throw new ValidationException("Not enough stock available for product: " + product.getName());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (item.getQuantity() + quantity > product.getStock()) {
                throw new ValidationException("Not enough stock available for product: " + product.getName());
            }
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than 0. If you want to remove item, use the remove endpoint.");
        }

        Cart cart = getCartByUserId(userId);
        
        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (itemToUpdate.getProduct().getStock() < quantity) {
            throw new ValidationException("Not enough stock available for product: " + itemToUpdate.getProduct().getName());
        }

        itemToUpdate.setQuantity(quantity);
        cartItemRepository.save(itemToUpdate);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);
        
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
