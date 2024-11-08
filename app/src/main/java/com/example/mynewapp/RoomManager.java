package com.example.mynewapp;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private static final String TAG = "RoomManager";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference countersRef = database.getReference("counters");
    private DatabaseReference matchesRef = database.getReference("matches");

    private int roomId;
    private int playerId;

    public void createOrJoinGame(final OnGameCreatedListener listener) {
        countersRef.child("0").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                roomId = snapshot.getValue(Integer.class);
                countersRef.child("0").setValue(roomId + 1);

                if (roomId % 2 == 0) {
                    playerId = 0;
                    Map<String, Object> gameData = new HashMap<>();
                    gameData.put("move", 0);
                    gameData.put("totalplayersjoined", 1);
                    gameData.put("isgameover", false);
                    gameData.put("winner", -1);
                    gameData.put("board", initializeBoard());

                    matchesRef.child(String.valueOf(roomId)).setValue(gameData);
                } else {
                    roomId -= 1;
                    playerId = 1;
                    matchesRef.child(String.valueOf(roomId)).child("totalplayersjoined").setValue(2);
                }

                Log.d(TAG, "Room ID: " + roomId + ", Player ID: " + playerId);
                listener.onGameCreated(roomId, playerId);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error creating or joining game: " + e.getMessage()));
    }

    private Map<String, Object> initializeBoard() {
        Map<String, Object> board = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            board.put(String.valueOf(i), new int[]{0, 0, 0, 0, 0, 0});
        }
        return board;
    }

    public interface OnGameCreatedListener {
        void onGameCreated(int roomId, int playerId);
    }
}
