package com.example.mynewapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Trouver le bouton dans le layout
        Button btnLogin = view.findViewById(R.id.btnLogin);

        // Ajouter un listener au bouton
        btnLogin.setOnClickListener(v -> {
            // Lancer l'activité HomeRamiActivity
            Intent intent = new Intent(getActivity(), HomeRamiActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
