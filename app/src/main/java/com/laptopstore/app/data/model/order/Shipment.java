package com.laptopstore.app.data.model.order;

import com.google.gson.annotations.SerializedName;

public class Shipment {
    @SerializedName("id")
    private Long id;

    @SerializedName("receiverName")
    private String receiverName;

    @SerializedName("receiverPhone")
    private String receiverPhone;

    @SerializedName("receiverAddress")
    private String receiverAddress;

    @SerializedName("trackingNumber")
    private String trackingNumber;

    @SerializedName("shippingProvider")
    private String shippingProvider;

    @SerializedName("status")
    private String status;

    public Long getId() { return id; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getReceiverAddress() { return receiverAddress; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getShippingProvider() { return shippingProvider; }
    public String getStatus() { return status; }
}
