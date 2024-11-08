package com.example.mynewapp.model;

public class Invitation {
    private String senderId;
    private String receiverId;

    public Invitation() {
        // Default constructor required for calls to DataSnapshot.getValue(Invitation.class)
    }

    public Invitation(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
