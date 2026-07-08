package com.smartpark.models;

import java.time.LocalDateTime;

public class Feedback {
    private int id;
    private int customerId;
    private int rating;
    private String comments;
    private LocalDateTime createdAt;

    public Feedback(int id, int customerId, int rating, String comments, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}