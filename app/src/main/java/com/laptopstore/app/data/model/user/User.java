package com.laptopstore.app.data.model.user;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class User {
    
    @SerializedName("id")
    private Long id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("address")
    private String address;

    @SerializedName("roles")
    private List<String> roles;

    @SerializedName("enabled")
    private Boolean enabled;

    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getAvatar() { return avatar; }
    public List<String> getRoles() { return roles; }
    public Boolean isEnabled() { return enabled != null ? enabled : true; }
    public String getCreatedAt() { return createdAt; }
}
