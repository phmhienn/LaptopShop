package com.laptopstore.app.data.model.order;

import com.google.gson.annotations.SerializedName;

public class OrderItem {

    @SerializedName("id")
    private Long id;

    @SerializedName("productId")
    private Long productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("productThumbnail")
    private String productThumbnail;

    @SerializedName("productPrice")
    private Double productPrice;

    @SerializedName("quantity")
    private Integer quantity;

    @SerializedName("subtotal")
    private Double subtotal;

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductThumbnail() {
        return productThumbnail;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }
}
