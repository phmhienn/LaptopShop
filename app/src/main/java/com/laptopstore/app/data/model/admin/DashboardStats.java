package com.laptopstore.app.data.model.admin;

import com.google.gson.annotations.SerializedName;

public class DashboardStats {
    @SerializedName("totalUsers")
    private long totalUsers;
    
    @SerializedName("totalProducts")
    private long totalProducts;
    
    @SerializedName("totalOrders")
    private long totalOrders;
    
    @SerializedName("totalRevenue")
    private Double totalRevenue;
    
    @SerializedName("newOrdersToday")
    private long newOrdersToday;
    
    @SerializedName("pendingReviews")
    private long pendingReviews;

    public long getTotalUsers() { return totalUsers; }
    public long getTotalProducts() { return totalProducts; }
    public long getTotalOrders() { return totalOrders; }
    public Double getTotalRevenue() { return totalRevenue; }
    public long getNewOrdersToday() { return newOrdersToday; }
    public long getPendingReviews() { return pendingReviews; }
}
