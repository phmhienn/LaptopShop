package com.laptopstore.app.data.model.product;

import com.google.gson.annotations.SerializedName;

public class Product {
    
    @SerializedName("id")
    private Long id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("slug")
    private String slug;
    
    @SerializedName("price")
    private Double price;
    
    @SerializedName("discountPrice")
    private Double discountPrice;
    
    @SerializedName("thumbnail")
    private String thumbnail;
    
    @SerializedName("brandName")
    private String brandName;
    
    @SerializedName("categoryName")
    private String categoryName;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("featured")
    private Boolean featured;
    
    @SerializedName("averageRating")
    private Double averageRating;
    
    @SerializedName("totalReviews")
    private Long totalReviews;

    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public Double getDiscountPrice() { return discountPrice; }
    public String getThumbnail() { return thumbnail; }
    public Double getAverageRating() { return averageRating; }
    
    public Double getEffectivePrice() {
        return discountPrice != null && discountPrice > 0 ? discountPrice : price;
    }
}
