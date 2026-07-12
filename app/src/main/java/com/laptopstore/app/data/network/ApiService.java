package com.laptopstore.app.data.network;

import com.laptopstore.app.data.model.ApiResponse;
import com.laptopstore.app.data.model.PagedResponse;
import com.laptopstore.app.data.model.auth.JwtResponse;
import com.laptopstore.app.data.model.auth.LoginRequest;
import com.laptopstore.app.data.model.auth.SignupRequest;
import com.laptopstore.app.data.model.order.Cart;
import com.laptopstore.app.data.model.product.Category;
import com.laptopstore.app.data.model.product.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //Full Retrofit
    // Auth
    @POST("auth/login")
    Call<ApiResponse<JwtResponse>> login(@Body LoginRequest request);
    @POST("auth/signup")
    Call<ApiResponse<Void>> signup(@Body SignupRequest request);

    // Products
    @GET("products")
    Call<ApiResponse<PagedResponse<Product>>> searchProducts(
            @Query("keyword") String keyword,
            @Query("brandId") Long brandId,
            @Query("categoryId") Long categoryId,
            @Query("minPrice") java.math.BigDecimal minPrice,
            @Query("maxPrice") java.math.BigDecimal maxPrice,
            @Query("ram") String ram,
            @Query("cpu") String cpu,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("products/latest")
    Call<ApiResponse<PagedResponse<Product>>> getLatestProducts(@Query("page") int page, @Query("size") int size);

    @GET("products/featured")
    Call<ApiResponse<PagedResponse<Product>>> getFeaturedProducts(@Query("page") int page, @Query("size") int size);
    
    @GET("products/category/{categoryId}")
    Call<ApiResponse<PagedResponse<Product>>> getProductsByCategory(@Path("categoryId") Long categoryId, @Query("page") int page, @Query("size") int size);

    // Categories
    @GET("products/id/{id}")
    Call<ApiResponse<com.laptopstore.app.data.model.product.ProductDetail>> getProductById(@Path("id") Long id);

    @GET("reviews/product/{productId}")
    Call<ApiResponse<PagedResponse<com.laptopstore.app.data.model.product.Review>>> getProductReviews(
        @Path("productId") Long productId,
        @Query("page") int page,
        @Query("size") int size
    );
    @POST("reviews")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Review>> createReview(@Body com.laptopstore.app.data.model.product.ReviewCreateRequest request);

    @GET("categories/tree")
    Call<ApiResponse<List<Category>>> getCategories();

    @GET("brands/active")
    Call<ApiResponse<List<com.laptopstore.app.data.model.product.Brand>>> getBrands();

    // Wishlist
    @GET("wishlists")
    Call<ApiResponse<List<Product>>> getWishlist();

    @POST("wishlists/{productId}")
    Call<ApiResponse<Void>> addToWishlist(@Path("productId") Long productId);

    @DELETE("wishlists/{productId}")
    Call<ApiResponse<Void>> removeFromWishlist(@Path("productId") Long productId);

    @GET("wishlists/{productId}/check")
    Call<ApiResponse<Boolean>> checkProductInWishlist(@Path("productId") Long productId);

    // Cart
    @GET("cart")
    Call<ApiResponse<Cart>> getCart();
    
    @POST("cart/items")
    Call<ApiResponse<Cart>> addItemToCart(@Body com.laptopstore.app.data.model.order.CartItemRequest request);

    @PUT("cart/items/{itemId}")
    Call<ApiResponse<Cart>> updateCartItemQuantity(@Path("itemId") Long itemId, @Query("quantity") int quantity);

    @DELETE("cart/items/{itemId}")
    Call<ApiResponse<Cart>> removeCartItem(@Path("itemId") Long itemId);

    // User Profile
    @GET("users/profile")
    Call<ApiResponse<com.laptopstore.app.data.model.user.User>> getProfile();

    @PUT("users/profile")
    Call<ApiResponse<com.laptopstore.app.data.model.user.User>> updateProfile(@Body com.laptopstore.app.data.model.user.UserProfileUpdateDTO request);

    // Orders
    @GET("orders/me")
    Call<ApiResponse<PagedResponse<com.laptopstore.app.data.model.order.Order>>> getMyOrders(@Query("page") int page, @Query("size") int size);

    @GET("orders/me/{id}")
    Call<ApiResponse<com.laptopstore.app.data.model.order.Order>> getMyOrderDetails(@Path("id") Long id);

    // Admin Endpoints
    @GET("admin/dashboard/stats")
    Call<ApiResponse<com.laptopstore.app.data.model.admin.DashboardStats>> getDashboardStats();

    // User - Orders
    @POST("orders/checkout")
    Call<ApiResponse<com.laptopstore.app.data.model.order.Order>> checkout(@Body com.laptopstore.app.data.model.order.CheckoutRequest request);

    // Admin - Orders
    @GET("orders")
    Call<ApiResponse<com.laptopstore.app.data.model.PagedResponse<com.laptopstore.app.data.model.order.Order>>> searchOrders(
        @Query("page") int page,
        @Query("size") int size
    );

    @GET("orders/{id}")
    Call<ApiResponse<com.laptopstore.app.data.model.order.Order>> getOrderById(
        @Path("id") Long id
    );

    @PUT("shipments/{id}/status")
    Call<ApiResponse<com.laptopstore.app.data.model.order.Shipment>> updateShipmentStatus(
        @Path("id") Long id,
        @Query("status") String status
    );

    // Admin - Brands
    @GET("brands")
    Call<ApiResponse<com.laptopstore.app.data.model.PagedResponse<com.laptopstore.app.data.model.product.Brand>>> getAllBrands(
        @Query("page") int page,
        @Query("size") int size
    );

    @GET("brands/id/{id}")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Brand>> getBrandById(
        @Path("id") Long id
    );

    @POST("brands")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Brand>> createBrand(
        @Body com.laptopstore.app.data.model.product.Brand brand
    );

    @PUT("brands/{id}")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Brand>> updateBrand(
        @Path("id") Long id,
        @Body com.laptopstore.app.data.model.product.Brand brand
    );

    @PATCH("brands/{id}/status")
    Call<ApiResponse<Void>> toggleBrandStatus(
        @Path("id") Long id
    );

    // Admin - Categories
    @GET("categories")
    Call<ApiResponse<com.laptopstore.app.data.model.PagedResponse<com.laptopstore.app.data.model.product.Category>>> getAllCategories(
        @Query("page") int page,
        @Query("size") int size
    );

    @GET("categories/id/{id}")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Category>> getCategoryById(
        @Path("id") Long id
    );

    @POST("categories")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Category>> createCategory(
        @Body com.laptopstore.app.data.model.product.Category category
    );

    @PUT("categories/{id}")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Category>> updateCategory(
        @Path("id") Long id,
        @Body com.laptopstore.app.data.model.product.Category category
    );

    @PATCH("categories/{id}/status")
    Call<ApiResponse<Void>> toggleCategoryStatus(
        @Path("id") Long id
    );

    // Admin - Products
    @GET("products")
    Call<ApiResponse<com.laptopstore.app.data.model.PagedResponse<com.laptopstore.app.data.model.product.Product>>> searchProducts(
        @Query("keyword") String keyword,
        @Query("page") int page,
        @Query("size") int size
    );

    @PATCH("products/{id}/status")
    Call<ApiResponse<Void>> toggleProductStatus(
        @Path("id") Long id
    );

    @PATCH("products/{id}/inventory")
    Call<ApiResponse<Void>> updateInventory(
        @Path("id") Long id,
        @Query("quantity") int quantity
    );

    @POST("products")
    @retrofit2.http.Multipart
    Call<ApiResponse<com.laptopstore.app.data.model.product.ProductDetail>> createProduct(
        @retrofit2.http.Part("product") okhttp3.RequestBody productJson,
        @retrofit2.http.Part("brandId") okhttp3.RequestBody brandId,
        @retrofit2.http.Part("categoryId") okhttp3.RequestBody categoryId
        // Omitting images for now to simplify
    );

    @PUT("products/{id}")
    @retrofit2.http.Multipart
    Call<ApiResponse<com.laptopstore.app.data.model.product.ProductDetail>> updateProduct(
        @Path("id") Long id,
        @retrofit2.http.Part("product") okhttp3.RequestBody productJson,
        @retrofit2.http.Part("brandId") okhttp3.RequestBody brandId,
        @retrofit2.http.Part("categoryId") okhttp3.RequestBody categoryId
    );

    // Admin - Users
    @GET("users")
    Call<ApiResponse<com.laptopstore.app.data.model.PagedResponse<com.laptopstore.app.data.model.user.User>>> getAllUsers(
        @Query("keyword") String keyword,
        @Query("page") int page,
        @Query("size") int size
    );

    @GET("users/{id}")
    Call<ApiResponse<com.laptopstore.app.data.model.user.User>> getUserById(
        @Path("id") Long id
    );

    @PATCH("users/{id}/status")
    Call<ApiResponse<Void>> toggleUserStatus(
        @Path("id") Long id
    );

    // Admin - Reviews
    @GET("reviews/pending")
    Call<ApiResponse<com.laptopstore.app.data.model.PagedResponse<com.laptopstore.app.data.model.product.Review>>> getPendingReviews(
        @Query("page") int page,
        @Query("size") int size
    );

    @PATCH("reviews/{id}/approve")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Review>> approveReview(
        @Path("id") Long id
    );

    @PATCH("reviews/{id}/reject")
    Call<ApiResponse<com.laptopstore.app.data.model.product.Review>> rejectReview(
        @Path("id") Long id
    );

    @DELETE("reviews/{id}")
    Call<ApiResponse<Void>> deleteReview(
        @Path("id") Long id
    );
}
