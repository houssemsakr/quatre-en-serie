package com.example.mynewapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewapp.model.UserModel;
import com.example.mynewapp.utils.FirebaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    private RecyclerView friendRecyclerView;
    private FriendAdapter friendAdapter;
    private List<UserModel> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        friendRecyclerView = findViewById(R.id.friend_recycler_view);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(this, friendList);
        friendRecyclerView.setAdapter(friendAdapter);

        loadFriends();

        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void loadFriends() {
        String currentUserId = FirebaseUtil.currentUserId();
        if (currentUserId != null) {
            FirebaseUtil.getDatabaseReference().child("users").child(currentUserId).child("amis")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            friendList.clear();
                            for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                                UserModel userModel = friendSnapshot.getValue(UserModel.class);
                                if (userModel != null) {
                                    userModel.setUserId(friendSnapshot.getKey());
                                    friendList.add(userModel);
                                }
                            }
                            friendAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(FriendActivity.this, "Erreur de chargement des amis", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
