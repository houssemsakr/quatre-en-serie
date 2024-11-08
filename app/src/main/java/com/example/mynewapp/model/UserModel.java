package com.example.mynewapp.model;

public class UserModel {
    private String uid;            // UID de l'utilisateur
    private String username;       // Nom d'utilisateur
    private String lastName;       // Nom de famille
    private String country;        // Pays
    private String userId;         // ID de l'utilisateur
    private String fcmToken;       // Token FCM pour les notifications
    private String profilePicUrl;  // URL de la photo de profil

    // Constructeur par défaut
    public UserModel() {
    }

    // Constructeur avec tous les champs
    public UserModel(String uid, String userId, String username, String lastName, String country, String profilePicUrl) {
        this.uid = uid;              // Initialisation de l'UID de l'utilisateur
        this.userId = userId;       // Initialisation de l'ID de l'utilisateur
        this.username = username;
        this.lastName = lastName;
        this.country = country;
        this.profilePicUrl = profilePicUrl;
    }

    // Constructeur sans uid et fcmToken (nouveau constructeur)
    public UserModel(String userId, String username, String lastName, String country, String profilePicUrl) {
        this.userId = userId;
        this.username = username;
        this.lastName = lastName;
        this.country = country;
        this.profilePicUrl = profilePicUrl;
    }

    public UserModel(String uid, String displayName) {
    }

    // Getters et Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
