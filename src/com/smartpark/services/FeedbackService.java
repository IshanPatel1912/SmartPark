package com.smartpark.services;

import com.smartpark.dao.FeedbackDAO;
import com.smartpark.models.Feedback;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FeedbackService {

    private FeedbackDAO feedbackDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
    }

    public void submitFeedback(int customerId, int rating, String comments) throws SQLException {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        Feedback feedback = new Feedback(0, customerId, rating, comments, LocalDateTime.now());
        feedbackDAO.addFeedback(feedback);
    }

    public void printAggregateFeedback() throws SQLException {
        List<Feedback> feedbacks = feedbackDAO.getAllFeedback();
        if (feedbacks.isEmpty()) {
            System.out.println("No feedback available yet.");
            return;
        }

        double totalRating = 0;
        System.out.println("\n--- Customer Feedback Report ---");
        for (Feedback f : feedbacks) {
            System.out.println("Customer ID: " + f.getCustomerId() + " | Rating: " + f.getRating() + "/5 | Date: " + f.getCreatedAt().toLocalDate());
            System.out.println("Comments: " + (f.getComments() != null && !f.getComments().isEmpty() ? f.getComments() : "No comments"));
            System.out.println("-");
            totalRating += f.getRating();
        }
        double avg = totalRating / feedbacks.size();
        System.out.printf("Total Feedback Entries: %d | Average System Rating: %.2f / 5.0%n", feedbacks.size(), avg);
    }
}