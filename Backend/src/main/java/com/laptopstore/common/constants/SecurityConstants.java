package com.laptopstore.common.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("Cannot instantiate constants class");
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_TYPE = "Bearer";

    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api/products/**",
            "/api/brands/**",
            "/api/categories/**",
            "/api/reviews/product/**",
            "/api/files/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**"
    };

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
}
