package com.laptopstore.app.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class SignupRequest {
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("fullName")
    private String fullName;

    public SignupRequest(String username, String email, String password, String fullName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Getters and setters omitted for brevity, but typically required for Retrofit/Gson
}
