package com.team_inertia.gonly_android.model;

import java.util.List;

public class ReviewResponse {
    private Long id;
    private Long gemId;
    private Long userId;
    private String userName;
    private Integer rating;
    private String comment;
    private Integer visitMonth;
    private String createdAt;
    private List<Long> imageIds;

    public Long getId() { return id; }
    public Long getGemId() { return gemId; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public Integer getVisitMonth() { return visitMonth; }
    public String getCreatedAt() { return createdAt; }
    public List<Long> getImageIds() { return imageIds; }
}