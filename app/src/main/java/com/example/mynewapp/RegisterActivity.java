package com.example.mynewapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword, inputConfirmPassword;
    private TextView tvLogin;
    private Button btnRegister;
    FirebaseAuth mAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Init
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        tvLogin = findViewById(R.id.tvLogin);
        btnRegister = findViewById(R.id.btnRegister); // Updated to correct ID

        // Firebase Init
        mAuth = FirebaseAuth.getInstance();

        // Move to login activity when the user clicks on "Already have an account? Login"
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });

        // Register button click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateDataAndDoRegister();
            }
        });
    }

    private void sendToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class); // Change to your correct activity
        startActivity(intent);
    }

    private void ValidateDataAndDoRegister() {
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (email.isEmpty()) {
            inputEmail.setError("Enter Email Address");
            inputEmail.requestFocus();
        } else if (!isValidEmail(email)) { // Validate email format
            inputEmail.setError("Enter valid email");
            inputEmail.requestFocus();
        } else if (password.isEmpty()) {
            inputPassword.setError("Enter Password");
            inputPassword.requestFocus();
        } else if (password.length() < 7) {
            inputPassword.setError("Password should be greater than 7 characters");
            inputPassword.requestFocus();
        } else if (confirmPassword.isEmpty()) {
            inputConfirmPassword.setError("Enter Confirm Password");
            inputConfirmPassword.requestFocus();
        } else if (confirmPassword.length() < 7) {
            inputConfirmPassword.setError("Password should be greater than 7 characters");
            inputConfirmPassword.requestFocus();
        } else if (!password.equals(confirmPassword)) {
            inputPassword.setError("Passwords do not match");
            inputPassword.requestFocus();
            inputConfirmPassword.setError("Passwords do not match");
            inputConfirmPassword.requestFocus();
            inputPassword.setText("");
            inputConfirmPassword.setText("");
        } else {
            doRegister(email, password);
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void doRegister(String email, String password) {
        btnRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendVerificationEmail(); // Send verification email
                } else {
                    btnRegister.setEnabled(true);
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        inputEmail.setError("Email Already Registered");
                        inputEmail.requestFocus();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void sendVerificationEmail() {
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        btnRegister.setEnabled(true);
                        goToActivitySecond(); // Redirection vers ActivitySecond après vérification
                    } else {
                        btnRegister.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void goToActivitySecond() {
        Intent intent = new Intent(getApplicationContext(), ActivitySecond.class); // Rediriger vers ActivitySecond
        intent.putExtra("email", email);
        startActivity(intent);
        finish(); // Terminer RegisterActivity
    }
}
