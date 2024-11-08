package com.example.mynewapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.mynewapp.model.UserModel;
import com.example.mynewapp.utils.FirebaseUtil;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySecond extends AppCompatActivity {

    private Button updateProfileBtn;
    private ProgressBar progressBar;
    private EditText usernameEditText, lastnameEditText;
    private CountryCodePicker countryCodePicker;
    private ImageView profileImageView;
    private Uri imageUri; // To store the selected image URI
    private boolean isInProgress = false;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Initialize UI elements
        updateProfileBtn = findViewById(R.id.updateProfileBtn);
        progressBar = findViewById(R.id.progressBar);
        usernameEditText = findViewById(R.id.profile_username);
        lastnameEditText = findViewById(R.id.profile_lastname);
        countryCodePicker = findViewById(R.id.profile_countrycode);
        profileImageView = findViewById(R.id.profile_image_view);

        // Initialize the image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData(); // Get the image URI
                        profileImageView.setImageURI(imageUri); // Show selected image
                    }
                }
        );

        // Set the click listener for the image view
        profileImageView.setOnClickListener(v -> selectImage());

        updateProfileBtn.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        // Configure the button action
        updateProfileBtn.setOnClickListener(v -> {
            if (!isInProgress) {
                startProfileUpdate();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void startProfileUpdate() {
        setInProgress(true);

        String username = usernameEditText.getText().toString().trim();
        String lastname = lastnameEditText.getText().toString().trim();
        String country = countryCodePicker.getSelectedCountryName();

        if (username.isEmpty() || lastname.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs requis.", Toast.LENGTH_SHORT).show();
            setInProgress(false);
            return;
        }

        String userId = FirebaseUtil.currentUserId();  // Obtenir l'ID de l'utilisateur

        // Utilisez l'URI d'image sélectionnée ou l'URL de l'image par défaut
        String profilePicUrl = (imageUri != null) ? imageUri.toString() : "drawable/ic_profile_picture"; // Image par défaut

        // Créer le modèle utilisateur (en utilisant le nouveau constructeur)
        UserModel userModel = new UserModel(userId, username, lastname, country, profilePicUrl);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(userId).setValue(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Toast.makeText(ActivitySecond.this, "Mise à jour du profil réussie!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            } else {
                Toast.makeText(ActivitySecond.this, "Erreur lors de la mise à jour du profil.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(ActivitySecond.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setInProgress(boolean inProgress) {
        this.isInProgress = inProgress;
        if (inProgress) {
            updateProfileBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            updateProfileBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
