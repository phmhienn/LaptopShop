package com.laptopstore.common.constants;

public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Cannot instantiate constants class");
    }

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    public static final int MAX_PAGE_SIZE = 100;

    public static final String PRODUCT_IMAGE_UPLOAD_DIR = "products";
    public static final String AVATAR_UPLOAD_DIR = "avatars";
    public static final String BRAND_LOGO_UPLOAD_DIR = "brands";
    public static final String CATEGORY_IMAGE_UPLOAD_DIR = "categories";

    public static final int MAX_COMPARE_PRODUCTS = 4;
    public static final int MAX_REVIEW_RATING = 5;
    public static final int MIN_REVIEW_RATING = 1;

    public static final String ORDER_CODE_PREFIX = "ORD";
    public static final String TRANSACTION_CODE_PREFIX = "TXN";
    public static final String TRACKING_NUMBER_PREFIX = "VN";

    public static final String DEFAULT_PAYMENT_METHOD = "COD";
    public static final String DEFAULT_WISHLIST_NAME = "My Wishlist";
}
