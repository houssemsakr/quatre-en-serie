package com.example.mynewapp;

public class User {
    private String uid;
    private String displayName;
    private String email;
    // Ajoutez d'autres champs selon vos besoins

    // Constructeur vide requis pour la désérialisation
    public User() {}

    public User(String uid, String displayName, String email) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
    }

    // Getters et setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
