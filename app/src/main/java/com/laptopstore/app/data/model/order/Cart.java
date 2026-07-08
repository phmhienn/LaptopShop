package com.laptopstore.app.data.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Cart {

    @SerializedName("id")
    private Long id;

    @SerializedName("items")
    private List<CartItem> items;

    @SerializedName("totalAmount")
    private Double totalAmount;

    @SerializedName("totalItems")
    private Integer totalItems;

    public Long getId() { return id; }
    public List<CartItem> getItems() { return items; }
    public Double getTotalAmount() { return totalAmount; }
    public Integer getTotalItems() { return totalItems; }
}
