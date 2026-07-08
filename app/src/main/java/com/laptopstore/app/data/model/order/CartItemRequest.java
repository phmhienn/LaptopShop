package com.laptopstore.app.data.model.order;

import com.google.gson.annotations.SerializedName;

public class CartItemRequest {
    
    @SerializedName("productId")
    private Long productId;
    
    @SerializedName("quantity")
    private Integer quantity;

    public CartItemRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
