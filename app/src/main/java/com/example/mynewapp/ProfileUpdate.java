package com.example.mynewapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction; // Import pour la transaction de fragment
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.example.mynewapp.model.UserModel;
import com.example.mynewapp.utils.AndroidUtil;
import com.example.mynewapp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileUpdate extends AppCompatActivity {

    private ImageView profilePic;
    private EditText usernameInput;
    private EditText lastNameInput;
    private EditText countryInput;
    private Button updateProfileBtn;
    private ProgressBar progressBar;

    private UserModel currentUserModel;
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile); // Assurez-vous que le bon layout est utilisé

        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        lastNameInput = findViewById(R.id.profile_lastname);
        countryInput = findViewById(R.id.profile_country);
        updateProfileBtn = findViewById(R.id.profile_update_btn);
        progressBar = findViewById(R.id.profile_progress_bar);

        getUserData();

        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick();
        });

        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        });

        // Initialiser le lanceur d'activités pour la sélection d'image
        imagePickLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(this, selectedImageUri, profilePic);
                        }
                    }
                }
        );
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        String newLastName = lastNameInput.getText().toString();
        String newCountry = countryInput.getText().toString();

        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }

        currentUserModel.setUsername(newUsername);
        currentUserModel.setLastName(newLastName);
        currentUserModel.setCountry(newCountry);
        setInProgress(true);

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> updateToRealtimeDatabase());
        } else {
            updateToRealtimeDatabase();
        }
    }

    void updateToRealtimeDatabase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseUtil.getCurrentUser().getUid());
        userRef.setValue(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(this, "Updated successfully");
                        // Naviguer vers le ProfileFragment
                        navigateToProfileFragment();
                    } else {
                        AndroidUtil.showToast(this, "Update failed");
                    }
                });
    }

    private void navigateToProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, profileFragment); // Assurez-vous que fragment_container est l'ID correct de votre conteneur de fragments
        transaction.addToBackStack(null); // Ajoute la transaction à la pile arrière
        transaction.commit();
    }

    void getUserData() {
        setInProgress(true);
        String userId = FirebaseUtil.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setInProgress(false);
                currentUserModel = dataSnapshot.getValue(UserModel.class);
                if (currentUserModel != null) {
                    usernameInput.setText(currentUserModel.getUsername());
                    lastNameInput.setText(currentUserModel.getLastName());
                    countryInput.setText(currentUserModel.getCountry());
                    if (currentUserModel.getProfilePicUrl() != null) {
                        AndroidUtil.setProfilePic(ProfileUpdate.this, Uri.parse(currentUserModel.getProfilePicUrl()), profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setInProgress(false);
                AndroidUtil.showToast(ProfileUpdate.this, "Error fetching user data: " + databaseError.getMessage());
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}
