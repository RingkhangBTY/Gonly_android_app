package com.team_inertia.gonly_android.model;

public class ReviewRequest {
    private Integer rating;
    private String comment;
    private Integer visitMonth;

    public ReviewRequest(Integer rating, String comment, Integer visitMonth) {
        this.rating = rating;
        this.comment = comment;
        this.visitMonth = visitMonth;
    }
}