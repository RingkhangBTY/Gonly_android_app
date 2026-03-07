package com.team_inertia.gonly_android.model;

import java.util.List;

public class GemResponse {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String locationSource;
    private String category;
    private String state;
    private String nearestTown;
    private String addressAuto;
    private String travelTips;
    private String howToReach;
    private String entryFee;
    private Integer bestSeasonStart;
    private Integer bestSeasonEnd;
    private String seasonWarning;
    private String difficultyLevel;
    private String safetyNote;
    private String localContact;
    private Boolean networkAvailable;
    private Double avgRating;
    private Integer reviewCount;
    private Integer viewCount;
    private String status;
    private String submittedByName;
    private Long submittedById;
    private String createdAt;
    private List<Long> imageIds;

    // GETTERS - simple and straightforward
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getLocationSource() { return locationSource; }
    public String getCategory() { return category; }
    public String getState() { return state; }
    public String getNearestTown() { return nearestTown; }
    public String getAddressAuto() { return addressAuto; }
    public String getTravelTips() { return travelTips; }
    public String getHowToReach() { return howToReach; }
    public String getEntryFee() { return entryFee; }
    public Integer getBestSeasonStart() { return bestSeasonStart; }
    public Integer getBestSeasonEnd() { return bestSeasonEnd; }
    public String getSeasonWarning() { return seasonWarning; }
    public String getDifficultyLevel() { return difficultyLevel; }
    public String getSafetyNote() { return safetyNote; }
    public String getLocalContact() { return localContact; }
    public Boolean getNetworkAvailable() { return networkAvailable; }
    public Double getAvgRating() { return avgRating; }
    public Integer getReviewCount() { return reviewCount; }
    public Integer getViewCount() { return viewCount; }
    public String getStatus() { return status; }
    public String getSubmittedByName() { return submittedByName; }
    public Long getSubmittedById() { return submittedById; }
    public String getCreatedAt() { return createdAt; }
    public List<Long> getImageIds() { return imageIds; }
}