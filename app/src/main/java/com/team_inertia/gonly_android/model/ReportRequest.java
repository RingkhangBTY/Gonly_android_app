package com.team_inertia.gonly_android.model;

public class ReportRequest {
    private Long gemId;
    private String reason;
    private String description;

    public ReportRequest(Long gemId, String reason, String description) {
        this.gemId = gemId;
        this.reason = reason;
        this.description = description;
    }

    @Override
    public String toString() {
        return "ReportRequest{" +
                "gemId=" + gemId +
                ", reason='" + reason + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
