package com.example.mynewapp; // Ajoutez cette ligne
import com.example.mynewapp.FriendActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

public class FragmentAmis extends Fragment {

    private ImageButton searchButton;
    private ImageButton invitationButton;
    private ImageButton friendsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.amis_fragment, container, false);

        searchButton = view.findViewById(R.id.main_search_btn);
        invitationButton = view.findViewById(R.id.invitation_icon);
        friendsButton = view.findViewById(R.id.friends_icon);

        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(getActivity(), SearchUserActivity.class));
        });

        invitationButton.setOnClickListener((v) -> {
            startActivity(new Intent(getActivity(), InvitationActivity.class));
        });

        friendsButton.setOnClickListener((v) -> {
            startActivity(new Intent(getActivity(), FriendActivity.class));
        });

        return view;
    }
}
