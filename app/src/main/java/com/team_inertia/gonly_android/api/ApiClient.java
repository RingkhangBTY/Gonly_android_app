package com.team_inertia.gonly_android.api;

import android.content.Context;
import com.team_inertia.gonly_android.util.SessionManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // CHANGE THIS to your backend URL
    // For emulator use: http://10.0.2.2:8080
    // For real phone on same wifi: http://YOUR_MAC_IP:8080  (e.g. http://192.168.1.5:8080)
    private static final String BASE_URL = "http://192.168.1.9:8080";

    private static Retrofit retrofit = null;

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            // This logs all API calls in Logcat - very helpful for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            SessionManager sessionManager = new SessionManager(context);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        // This automatically adds JWT token to every request
                        Request original = chain.request();
                        String token = sessionManager.getToken();

                        if (token != null) {
                            // Add "Authorization: Bearer <token>" header
                            Request newRequest = original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .build();
                            return chain.proceed(newRequest);
                        }

                        return chain.proceed(original);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }

    // Call this when user logs out so we create fresh Retrofit with no token
    public static void resetClient() {
        retrofit = null;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}