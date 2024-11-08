package com.example.mynewapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.mynewapp.model.UserModel; // Import de UserModel
import com.example.mynewapp.utils.AndroidUtil; // Import de AndroidUtil

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Vérification de l'utilisateur connecté
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("SplashActivity", "Current User: " + currentUser);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserModel model = dataSnapshot.getValue(UserModel.class);
                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        AndroidUtil.passUserModelAsIntent(mainIntent, model);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Log.d("SplashActivity", "User data not found.");
                        AndroidUtil.showToast(SplashActivity.this, "User data not found");
                        redirectToRegister();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("SplashActivity", "Database error: " + databaseError.getMessage());
                    AndroidUtil.showToast(SplashActivity.this, "Failed to load user data");
                    redirectToRegister();
                }
            });
        } else {
            Log.d("SplashActivity", "User is not logged in.");
            redirectToRegister();  // Redirection vers RegisterActivity si l'utilisateur n'est pas connecté
        }
    }

    // Redirection vers l'activité d'inscription
    private void redirectToRegister() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
            finish();
        }, 500); // Temporisation de 500 ms pour le débogage
    }
}
