package com.laptopstore.app.data.model.product;

import com.google.gson.annotations.SerializedName;

public class Review {
    
    @SerializedName("id")
    private Long id;
    
    @SerializedName("productId")
    private Long productId;
    
    @SerializedName("productName")
    private String productName;
    
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("rating")
    private Integer rating;
    
    @SerializedName("comment")
    private String comment;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}
