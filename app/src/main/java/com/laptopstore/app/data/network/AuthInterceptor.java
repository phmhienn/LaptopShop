package com.laptopstore.app.data.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        String accessToken = tokenManager.getAccessToken();
        
        // If there's no token, just proceed with the original request
        if (accessToken == null || accessToken.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        // Add the Authorization header
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .build();

        return chain.proceed(newRequest);
    }
}
