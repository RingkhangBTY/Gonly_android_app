package com.team_inertia.gonly_android.model;

public class GemRequest {

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

    public GemRequest() {}

    // === GETTERS ===
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

    // === SETTERS ===
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setLocationSource(String locationSource) { this.locationSource = locationSource; }
    public void setCategory(String category) { this.category = category; }
    public void setState(String state) { this.state = state; }
    public void setNearestTown(String nearestTown) { this.nearestTown = nearestTown; }
    public void setAddressAuto(String addressAuto) { this.addressAuto = addressAuto; }
    public void setTravelTips(String travelTips) { this.travelTips = travelTips; }
    public void setHowToReach(String howToReach) { this.howToReach = howToReach; }
    public void setEntryFee(String entryFee) { this.entryFee = entryFee; }
    public void setBestSeasonStart(Integer bestSeasonStart) { this.bestSeasonStart = bestSeasonStart; }
    public void setBestSeasonEnd(Integer bestSeasonEnd) { this.bestSeasonEnd = bestSeasonEnd; }
    public void setSeasonWarning(String seasonWarning) { this.seasonWarning = seasonWarning; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public void setSafetyNote(String safetyNote) { this.safetyNote = safetyNote; }
    public void setLocalContact(String localContact) { this.localContact = localContact; }
    public void setNetworkAvailable(Boolean networkAvailable) { this.networkAvailable = networkAvailable; }
}