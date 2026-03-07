package com.team_inertia.gonly_android.model;

import java.util.List;

public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private String locationSource;
    private String state;
    private String eventType;
    private String startDate;
    private String endDate;
    private String status;
    private String submittedByName;
    private Long submittedById;
    private String createdAt;
    private List<Long> imageIds;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getState() { return state; }
    public String getEventType() { return eventType; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }
    public String getSubmittedByName() { return submittedByName; }
    public String getCreatedAt() { return createdAt; }
    public List<Long> getImageIds() { return imageIds; }
}