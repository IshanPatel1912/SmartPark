package com.smartpark.dao;

import com.smartpark.models.Feedback;
import com.smartpark.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    public void addFeedback(Feedback feedback) throws SQLException {
        String query = "INSERT INTO feedback (customer_id, rating, comments, created_at) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, feedback.getCustomerId());
            stmt.setInt(2, feedback.getRating());
            stmt.setString(3, feedback.getComments());
            stmt.setTimestamp(4, Timestamp.valueOf(feedback.getCreatedAt()));
            stmt.executeUpdate();
        }
    }

    public List<Feedback> getAllFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT * FROM feedback ORDER BY created_at DESC";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                feedbackList.add(new Feedback(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("rating"),
                        rs.getString("comments"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return feedbackList;
    }
}