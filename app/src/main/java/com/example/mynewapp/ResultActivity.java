package com.example.mynewapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultText = findViewById(R.id.resultText);
        Button playAgainButton = findViewById(R.id.playAgainButton);

        boolean isWinner = getIntent().getBooleanExtra("isWinner", false);
        resultText.setText(isWinner ? "You Won!" : "You Lost!");

        playAgainButton.setOnClickListener(v -> {
            startActivity(new Intent(ResultActivity.this, GameActivity.class));
            finish();
        });
    }
}
