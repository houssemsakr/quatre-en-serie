package com.example.mynewapp.utils;

import android.net.Uri;
import androidx.annotation.NonNull;
import com.example.mynewapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUtil {
    private static FirebaseAuth auth;
    private static StorageReference storageReference;
    private static DatabaseReference databaseReference;

    // Initialization of Firebase
    static {
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // Method to get the database reference
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    // Get the current authenticated user
    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // Get the current user ID
    public static String currentUserId() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    // Get reference to the current user's profile picture in storage
    public static StorageReference getCurrentProfilePicStorageRef() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            return storageReference.child("profile_pictures").child(user.getUid() + ".jpg");
        }
        return null;
    }

    // Get the reference to another user's profile picture
    public static StorageReference getOtherProfilePicStorageRef(String userId) {
        return storageReference.child("profile_pictures").child(userId + ".jpg");
    }

    // Get the current user's details from the realtime database
    public static DatabaseReference currentUserDetails() {
        return databaseReference.child("users").child(currentUserId());
    }

    // Envoyer une invitation d'ami
    public static void sendFriendInvitation(String receiverId, UserModel senderModel) {
        String invitationId = databaseReference.child("users").child(receiverId).child("invitations").push().getKey();

        if (invitationId != null) {
            Map<String, Object> invitationData = new HashMap<>();
            invitationData.put("senderId", currentUserId());
            invitationData.put("username", senderModel.getUsername());
            invitationData.put("lastName", senderModel.getLastName());
            invitationData.put("profilePicUrl", senderModel.getProfilePicUrl());

            databaseReference.child("users").child(receiverId).child("invitations").child(invitationId).setValue(invitationData)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            // Gérer l'échec d'envoi de l'invitation
                        }
                    });
        }
    }

    // Get the invitations reference for a user
    public static DatabaseReference getInvitationsRef(String userId) {
        return databaseReference.child("users").child(userId).child("invitations");
    }
    public static String createGameInFirebase(String playerXId, String playerOId) {
        String gameId = FirebaseDatabase.getInstance().getReference("games").push().getKey();
        if (gameId != null) {
            Map<String, Object> gameData = new HashMap<>();
            gameData.put("playerXId", playerXId);
            gameData.put("playerOId", playerOId);
            gameData.put("activePlayer", playerXId); // Player X starts the game
            gameData.put("gameState", new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2});
            FirebaseDatabase.getInstance().getReference("games").child(gameId).setValue(gameData);
        }
        return gameId;
    }

}
