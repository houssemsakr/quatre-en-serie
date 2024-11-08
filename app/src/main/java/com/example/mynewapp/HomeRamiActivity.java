package com.example.mynewapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeRamiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_rami);  // Assurez-vous de définir le bon fichier de mise en page

        // Récupérez le bouton depuis la vue
        Button startGameButton = findViewById(R.id.button_start_game);

        // Configurez le clic pour aller vers GameActivity
        startGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeRamiActivity.this, GameActivity.class);
            startActivity(intent);
        });
    }
}
