package com.laptopstore.app.data.model.product;

import com.google.gson.annotations.SerializedName;

public class ReviewCreateRequest {
    @SerializedName("productId")
    private Long productId;

    @SerializedName("rating")
    private Integer rating;

    @SerializedName("comment")
    private String comment;

    public ReviewCreateRequest(Long productId, Integer rating, String comment) {
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
