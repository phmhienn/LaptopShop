package com.laptopstore.app.data.model.product;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductDetail {
    
    @SerializedName("id")
    private Long id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("cpu")
    private String cpu;
    
    @SerializedName("ram")
    private String ram;
    
    @SerializedName("ssd")
    private String ssd;
    
    @SerializedName("gpu")
    private String gpu;
    
    @SerializedName("display")
    private String display;
    
    @SerializedName("price")
    private Double price;
    
    @SerializedName("discountPrice")
    private Double discountPrice;
    
    @SerializedName("thumbnail")
    private String thumbnail;
    
    @SerializedName("images")
    private List<ProductImage> images;

    @SerializedName("brand")
    private Brand brand;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCpu() { return cpu; }
    public String getRam() { return ram; }
    public String getSsd() { return ssd; }
    public String getGpu() { return gpu; }
    public String getDisplay() { return display; }
    public Double getPrice() { return price; }
    public Double getDiscountPrice() { return discountPrice; }
    public String getThumbnail() { return thumbnail; }
    public List<ProductImage> getImages() { return images; }
    public Brand getBrand() { return brand; }
    
    public Double getEffectivePrice() {
        return discountPrice != null && discountPrice > 0 ? discountPrice : price;
    }

    public static class ProductImage {
        @SerializedName("id")
        private Long id;
        
        @SerializedName("imageUrl")
        private String imageUrl;

        public String getImageUrl() { return imageUrl; }
    }
}
