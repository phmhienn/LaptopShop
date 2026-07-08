package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.order.CartDTO;
import com.laptopstore.application.dto.order.CartItemDTO;
import com.laptopstore.data.entity.Cart;
import com.laptopstore.data.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartDTO toCartDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());

        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            List<CartItemDTO> items = cart.getItems().stream()
                    .map(this::toCartItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(items);
            
            BigDecimal totalAmount = items.stream()
                    .map(CartItemDTO::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalAmount(totalAmount);
            
            Integer totalItems = items.stream()
                    .map(CartItemDTO::getQuantity)
                    .reduce(0, Integer::sum);
            dto.setTotalItems(totalItems);
        } else {
            dto.setItems(Collections.emptyList());
            dto.setTotalAmount(BigDecimal.ZERO);
            dto.setTotalItems(0);
        }

        return dto;
    }

    public CartItemDTO toCartItemDTO(CartItem item) {
        if (item == null) {
            return null;
        }

        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());

        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setProductThumbnail(item.getProduct().getThumbnail());
            
            BigDecimal price = item.getProduct().getEffectivePrice();
            dto.setPrice(price);
            dto.setSubtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        return dto;
    }
}
