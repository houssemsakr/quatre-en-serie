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

public class InvitationActivity extends AppCompatActivity {

    private RecyclerView invitationRecyclerView;
    private InvitationAdapter invitationAdapter;
    private List<UserModel> invitationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        // Initialisation du RecyclerView
        invitationRecyclerView = findViewById(R.id.invitation_recycler_view);
        invitationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        invitationList = new ArrayList<>();
        invitationAdapter = new InvitationAdapter(this, invitationList);
        invitationRecyclerView.setAdapter(invitationAdapter);

        // Charger les invitations depuis Firebase
        loadInvitations();

        // Bouton retour avec listener pour appeler onBackPressed
        ImageButton backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(view -> onBackPressed());
    }

    // Fonction pour charger les invitations depuis Firebase
    private void loadInvitations() {
        String currentUserId = FirebaseUtil.currentUserId();
        if (currentUserId != null) {
            FirebaseUtil.getDatabaseReference().child("users").child(currentUserId).child("invitations")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            invitationList.clear();
                            for (DataSnapshot invitationSnapshot : snapshot.getChildren()) {
                                UserModel userModel = invitationSnapshot.getValue(UserModel.class);
                                if (userModel != null) {
                                    userModel.setUserId(invitationSnapshot.getKey()); // Assurez-vous d'utiliser la clé correcte comme ID utilisateur
                                    invitationList.add(userModel);
                                }
                            }
                            invitationAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(InvitationActivity.this, "Erreur de chargement des invitations", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
