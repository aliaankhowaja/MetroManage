package com.metromanage.domain;

import java.time.LocalDateTime;

import com.metromanage.model.FeedbackPersistanceHandler;

public class Feedback {
    int feedbackID;
    int passengerID;
    String type;
    String comments;
    LocalDateTime timestamp;

    public Feedback(int passengerID, String type, String comments) {
        this.passengerID = passengerID;
        this.type = type;
        this.comments = comments;
        this.timestamp = LocalDateTime.now();
        FeedbackPersistanceHandler fph = new FeedbackPersistanceHandler();
        this.feedbackID = fph.save(this);
    }

    public Feedback() {

    }
    
    public int getFeedbackID() {
        return feedbackID;
    }

    public int getPassengerID() {
        return passengerID;
    }

    public String getType() {
        return type;
    }

    public String getComments() {
        return comments;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }

    public void setPassengerID(int passengerID) {
        this.passengerID = passengerID;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    

    
}
