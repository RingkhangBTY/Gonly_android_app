package com.team_inertia.gonly_android.model;

public class LoginResponse {
    private String token;
    private String email;
    private String fullName;
    private Long userId;

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public Long getUserId() { return userId; }
}