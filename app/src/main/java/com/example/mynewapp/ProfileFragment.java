package com.example.mynewapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; // Importer Log ici
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.example.mynewapp.model.UserModel;
import com.example.mynewapp.utils.AndroidUtil;
import com.example.mynewapp.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText lastNameInput;
    EditText countryInput;
    Button updateProfileBtn;
    ProgressBar progressBar;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            // Charger l'image sélectionnée avec Glide
                            Glide.with(getContext())
                                    .load(selectedImageUri)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.error)
                                    .into(profilePic);
                        } else {
                            Log.e("ProfileFragment", "Image data is null");
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        lastNameInput = view.findViewById(R.id.profile_lastname);
        countryInput = view.findViewById(R.id.profile_country);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);

        // Fetch user data
        getUserData();

        // Navigate to ProfileUpdate Activity when the button is clicked
        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClick(); // Call to update user data
        });

        return view;
    }

    void updateBtnClick() {
        String newUsername = usernameInput.getText().toString();
        String newLastName = lastNameInput.getText().toString();
        String newCountry = countryInput.getText().toString();

        // Validate inputs
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            Log.e("ProfileFragment", "Invalid username: " + newUsername);
            return;
        }

        // Update user model
        currentUserModel.setUsername(newUsername);
        currentUserModel.setLastName(newLastName);
        currentUserModel.setCountry(newCountry);
        setInProgress(true);

        // Check if a new image has been selected and update the profile
        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateToRealtimeDatabase();
                        } else {
                            setInProgress(false);
                            Log.e("ProfileFragment", "Image upload failed: " + task.getException().getMessage());
                            AndroidUtil.showToast(getContext(), "Image upload failed: " + task.getException().getMessage());
                        }
                    });
        } else {
            updateToRealtimeDatabase();
        }

        // Navigate to ProfileUpdateActivity
        Intent intent = new Intent(getActivity(), ProfileUpdate.class);
        startActivity(intent);
    }

    void updateToRealtimeDatabase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseUtil.getCurrentUser().getUid());
        userRef.setValue(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Log.d("ProfileFragment", "User data updated successfully");
                        AndroidUtil.showToast(getContext(), "Updated successfully");
                    } else {
                        Log.e("ProfileFragment", "Update failed: " + task.getException().getMessage());
                        AndroidUtil.showToast(getContext(), "Update failed: " + task.getException().getMessage());
                    }
                });
    }

    void getUserData() {
        setInProgress(true);
        String userId = FirebaseUtil.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Using a separate thread for UI updates
                getActivity().runOnUiThread(() -> {
                    setInProgress(false);
                    currentUserModel = dataSnapshot.getValue(UserModel.class);
                    if (currentUserModel != null) {
                        usernameInput.setText(currentUserModel.getUsername());
                        lastNameInput.setText(currentUserModel.getLastName());
                        countryInput.setText(currentUserModel.getCountry());

                        String profilePicUrl = currentUserModel.getProfilePicUrl();
                        Log.d("ProfileFragment", "Profile Pic URL: " + profilePicUrl);

                        if (profilePicUrl != null) {
                            Glide.with(getContext())
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.error)
                                    .into(profilePic);
                        } else {
                            Log.e("ProfileFragment", "Profile picture URL is null");
                        }
                    } else {
                        Log.e("ProfileFragment", "Current user model is null");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setInProgress(false);
                Log.e("ProfileFragment", "Error fetching user data: " + databaseError.getMessage());
                AndroidUtil.showToast(getContext(), "Error fetching user data: " + databaseError.getMessage());
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
            Log.d("ProfileFragment", "Loading in progress...");
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
            Log.d("ProfileFragment", "Loading finished.");
        }
    }
}
