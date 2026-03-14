package com.team_inertia.gonly_android.api;

import com.team_inertia.gonly_android.model.*;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ==================== AUTH ====================

    // POST /api/auth/register — no token needed
    @POST("/api/auth/register")
    Call<ApiResponse> register(@Body RegisterRequest request);

    // POST /api/auth/login — no token needed
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // GET /api/auth/profile — token needed
    @GET("/api/auth/profile")
    Call<Map<String, Object>> getProfile();

    // ==================== GEMS ====================

    // GET /api/gems — get all approved gems
    @GET("/api/gems")
    Call<List<GemResponse>> getAllGems();

    // GET /api/gems/{id} — get single gem
    @GET("/api/gems/{id}")
    Call<GemResponse> getGemById(@Path("id") long id);

    // GET /api/gems/search?q= — smart search
    @GET("/api/gems/search")
    Call<List<GemResponse>> searchGems(@Query("q") String query);

    // GET /api/gems/category?type= — filter by category
    @GET("/api/gems/category")
    Call<List<GemResponse>> filterByCategory(@Query("type") String type);

    // GET /api/gems/state?name= — filter by state
    @GET("/api/gems/state")
    Call<List<GemResponse>> filterByState(@Query("name") String name);

    // GET /api/gems/{gemId}/images/all — get all image for a gem
    @GET("api/gems/{gemId}/images/all")
    Call<List<GemImageData>> getAllGemImages(@Path("gemId") long gemId);

    // GET /api/gems/nearby — find nearby gems using GPS
    @GET("/api/gems/nearby")
    Call<List<GemResponse>> getNearbyGems(
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("radiusKm") double radiusKm);

    // POST /api/gems — create new gem (token needed)
    @POST("/api/gems")
    Call<GemResponse> createGem(@Body GemRequest request);

    // GET /api/gems/my — my submitted gems (token needed)
    @GET("/api/gems/my")
    Call<List<GemResponse>> getMyGems();

    // POST /api/gems/{id}/images — upload image to gem (token needed)
    @Multipart
    @POST("/api/gems/{id}/images")
    Call<ApiResponse> uploadGemImage(
            @Path("id") long gemId,
            @Part MultipartBody.Part image);

    // GET /api/gems/images/{imageId} — get image bytes (used as URL in Glide)
    // We don't call this via Retrofit - we load it directly with Glide using the URL

    // ==================== REVIEWS ====================

    // GET /api/gems/{gemId}/reviews — get reviews for a gem
    @GET("/api/gems/{gemId}/reviews")
    Call<List<ReviewResponse>> getReviews(@Path("gemId") long gemId);

    // POST /api/gems/{gemId}/reviews — add review (token needed)
    @POST("/api/gems/{gemId}/reviews")
    Call<ReviewResponse> addReview(@Path("gemId") long gemId, @Body ReviewRequest request);

    // ==================== BOOKMARKS ====================

    // GET /api/bookmarks — my bookmarked gems (token needed)
    @GET("/api/bookmarks")
    Call<List<GemResponse>> getMyBookmarks();

    // POST /api/bookmarks/{gemId} — toggle bookmark (token needed)
    @POST("/api/bookmarks/{gemId}")
    Call<ApiResponse> toggleBookmark(@Path("gemId") long gemId);

    // GET /api/bookmarks/check/{gemId} — check if bookmarked (token needed)
    @GET("/api/bookmarks/check/{gemId}")
    Call<ApiResponse> checkBookmark(@Path("gemId") long gemId);

    // ==================== EVENTS ====================

    // GET /api/events — get all events
    @GET("/api/events")
    Call<List<EventResponse>> getAllEvents();

    // POST /api/events — create event (token needed)
    @POST("/api/events")
    Call<EventResponse> createEvent(@Body EventRequest request);

    // ================ report ================
    @POST("api/reports")
    Call<ApiResponse> createReport(@Body ReportRequest report);

    @DELETE("/api/gems/{id}")
    Call<ApiResponse> deleteGem(@Path("id") Long id);
}