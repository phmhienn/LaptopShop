package com.laptopstore.app.data.model.order;

import com.google.gson.annotations.SerializedName;

public class CheckoutRequest {

    @SerializedName("shippingName")
    private String shippingName;

    @SerializedName("shippingPhone")
    private String shippingPhone;

    @SerializedName("shippingAddress")
    private String shippingAddress;

    @SerializedName("note")
    private String note;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("couponCode")
    private String couponCode;

    public CheckoutRequest() {
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
