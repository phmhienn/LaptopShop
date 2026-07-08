package com.laptopstore.app.data.model.order;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("id")
    private Long id;

    @SerializedName("orderCode")
    private String orderCode;

    @SerializedName("totalAmount")
    private Double totalAmount;

    @SerializedName("finalAmount")
    private Double finalAmount;

    @SerializedName("shipment")
    private Shipment shipment;


    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("items")
    private java.util.List<OrderItem> items;

    public Long getId() { return id; }
    public String getOrderCode() { return orderCode; }
    public Double getTotalAmount() { return totalAmount; }
    public Double getFinalAmount() { return finalAmount; }
    public Shipment getShipment() { return shipment; }
    
    // Convenience getters to avoid crashing old UI code
    public String getShippingName() { return shipment != null ? shipment.getReceiverName() : ""; }
    public String getShippingPhone() { return shipment != null ? shipment.getReceiverPhone() : ""; }
    public String getShippingAddress() { return shipment != null ? shipment.getReceiverAddress() : ""; }
    
    public String getStatus() { return shipment != null ? shipment.getStatus() : ""; }
    public String getCreatedAt() { return createdAt; }
    public java.util.List<OrderItem> getItems() { return items; }
}
