package com.laptopstore.app.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenManager {

    private static final String PREF_NAME = "LaptopStoreAuth";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_FULL_NAME = "user_full_name";
    private static final String KEY_USER_ROLES = "user_roles";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveToken(String accessToken, String refreshToken) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public void saveUserInfo(Long userId, String fullName, List<String> roles) {
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER_FULL_NAME, fullName);
        if (roles != null) {
            editor.putStringSet(KEY_USER_ROLES, new HashSet<>(roles));
        }
        editor.apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public Long getUserId() {
        long id = prefs.getLong(KEY_USER_ID, -1);
        return id != -1 ? id : null;
    }

    public String getUserFullName() {
        return prefs.getString(KEY_USER_FULL_NAME, null);
    }

    public boolean isAdmin() {
        Set<String> roles = prefs.getStringSet(KEY_USER_ROLES, new HashSet<>());
        return roles.contains("ROLE_ADMIN");
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
