package com.team_inertia.gonly_android.model;

public class EventRequest {
    private String title;
    private String description;
    private Double latitude;
    private Double longitude;
    private String locationSource;
    private String state;
    private String eventType;
    private String startDate;
    private String endDate;

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setLocationSource(String locationSource) { this.locationSource = locationSource; }
    public void setState(String state) { this.state = state; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}