package com.laptopstore.app.data.model.user;

import com.google.gson.annotations.SerializedName;

public class UserProfileUpdateDTO {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;
    
    @SerializedName("address")
    private String address;

    @SerializedName("currentPassword")
    private String currentPassword;

    @SerializedName("newPassword")
    private String newPassword;

    public UserProfileUpdateDTO(String fullName, String phone, String address, String currentPassword, String newPassword) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
