package com.example.mynewapp.model;

public class Friend {
    private String userId;
    private String username;
    private String lastName;
    private String profilePicUrl;

    public Friend() {
        // Constructeur par défaut requis pour les appels à DataSnapshot.getValue(Friend.class)
    }

    public Friend(String userId, String username, String lastName, String profilePicUrl) {
        this.userId = userId;
        this.username = username;
        this.lastName = lastName;
        this.profilePicUrl = profilePicUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
