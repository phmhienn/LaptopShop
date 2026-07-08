package com.laptopstore.app.data.model.auth;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JwtResponse {
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("refreshToken")
    private String refreshToken;
    
    @SerializedName("id")
    private Long id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("roles")
    private List<String> roles;

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public List<String> getRoles() { return roles; }
}
