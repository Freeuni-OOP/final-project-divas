package org.example.quiz.model;

import java.sql.Timestamp;

public class Achievement {
    private long id;
    private long userId;
    private String achievementCode;
    private Timestamp earnedAt;

    private String label;
    private String description;

    public Achievement() { }

    public Achievement(long userId, String achievementCode) {
        this.userId = userId;
        this.achievementCode = achievementCode;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getAchievementCode() { return achievementCode; }
    public void setAchievementCode(String achievementCode) { this.achievementCode = achievementCode; }

    public Timestamp getEarnedAt() { return earnedAt; }
    public void setEarnedAt(Timestamp earnedAt) { this.earnedAt = earnedAt; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}