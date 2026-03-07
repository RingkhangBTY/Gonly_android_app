package com.team_inertia.gonly_android.model;

public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String homeState;
    private String bio;

    public RegisterRequest(String email, String password, String fullName, String homeState, String bio) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.homeState = homeState;
        this.bio = bio;
    }
}