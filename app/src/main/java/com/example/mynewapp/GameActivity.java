package com.example.mynewapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private int roomId;
    private int playerId;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference matchRef;
    private GameBoard gameBoard = new GameBoard();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        RoomManager roomManager = new RoomManager();
        roomManager.createOrJoinGame((roomId, playerId) -> {
            this.roomId = roomId;
            this.playerId = playerId;
            Log.d(TAG, "Joined room ID: " + roomId + " as player ID: " + playerId);

            matchRef = database.getReference("matches").child(String.valueOf(roomId));
            listenToGameUpdates();
        });
    }

    private void listenToGameUpdates() {
        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                Map<String, Object> boardData = (Map<String, Object>) snapshot.child("board").getValue();
                if (boardData != null) {
                    for (int i = 0; i < 7; i++) {
                        int[] row = (int[]) boardData.get(String.valueOf(i));
                        System.arraycopy(row, 0, gameBoard.getBoard()[i], 0, row.length);
                    }
                }

                int move = snapshot.child("move").getValue(Integer.class);
                int totalPlayers = snapshot.child("totalplayersjoined").getValue(Integer.class);
                boolean isGameOver = snapshot.child("isgameover").getValue(Boolean.class);
                int winner = snapshot.child("winner").getValue(Integer.class);

                if (isGameOver) {
                    showResult(winner == playerId);
                } else if (totalPlayers < 2) {
                    showWaiting();
                } else {
                    updateGameBoard();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void updateGameBoard() {
        Log.d(TAG, "Updating game board for player " + playerId);
        // Code to update the game board UI
    }

    private void makeMove(int row, int col) {
        if (gameBoard.makeMove(playerId, row, col)) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("board", gameBoard.getBoard());
            updates.put("move", (playerId + 1) % 2);
            updates.put("isgameover", true);
            updates.put("winner", playerId);
            matchRef.updateChildren(updates);

            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("isWinner", true);
            startActivity(intent);
            finish();
        }
    }

    private void showResult(boolean won) {
        String result = won ? "You won!" : "You lost!";
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showWaiting() {
        Toast.makeText(this, "Waiting for another player...", Toast.LENGTH_SHORT).show();
    }

    public void onLeaveGameClicked(View view) {
        Toast.makeText(this, "Leaving the game...", Toast.LENGTH_SHORT).show();
        finish();
    }
}
