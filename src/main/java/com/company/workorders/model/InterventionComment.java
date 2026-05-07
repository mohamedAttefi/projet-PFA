package com.company.workorders.model;

import java.time.LocalDateTime;

/**
 * Model class representing comments on interventions
 */
public class InterventionComment {
    private long id;
    private long interventionId;
    private long userId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String commentType; // "INTERNAL", "CLIENT", "STATUS_UPDATE", "NOTE"

    public InterventionComment() {}

    public InterventionComment(long id, long interventionId, long userId, String userName, 
                            String content, LocalDateTime createdAt, LocalDateTime updatedAt, String commentType) {
        this.id = id;
        this.interventionId = interventionId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentType = commentType;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInterventionId() {
        return interventionId;
    }

    public void setInterventionId(long interventionId) {
        this.interventionId = interventionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    @Override
    public String toString() {
        return "InterventionComment{" +
                "id=" + id +
                ", interventionId=" + interventionId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", commentType='" + commentType + '\'' +
                '}';
    }
}
