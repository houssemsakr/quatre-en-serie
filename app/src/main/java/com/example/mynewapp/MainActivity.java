package com.example.mynewapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;  // Import FirebaseAuth pour la déconnexion

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Assurez-vous que le nom du fichier XML est correct

        // Configurez la Toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Définir le titre de l'application
        TextView appTitle = findViewById(R.id.app_title);
        appTitle.setText(R.string.app_name); // Titre de l'application, récupéré à partir des ressources

        // Configurez l'icône de déconnexion
        ImageView logoutIcon = findViewById(R.id.logout_icon);
        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(); // Appelez la méthode de déconnexion
            }
        });

        // Définir le comportement pour les clics dans la barre de navigation inférieure
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        selectedFragment = new HomeFragment(); // Fragment pour la page d'accueil
                        break;
                    case R.id.menu_profile:
                        selectedFragment = new ProfileFragment(); // Fragment pour la page de profil
                        break;
                    case R.id.menu_friend:
                        selectedFragment = new FragmentAmis(); // Fragment pour les amis
                        break;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });

        // Chargez par défaut la page d'accueil
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private void logout() {
        // Déconnexion de Firebase
        FirebaseAuth.getInstance().signOut();

        // Rediriger vers l'écran d'inscription
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Terminez cette activité
    }
}
