package com.example.mynewapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mynewapp.FragmentAmis;
import com.example.mynewapp.R;
import com.example.mynewapp.model.UserModel;
import com.example.mynewapp.utils.AndroidUtil;
import com.example.mynewapp.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> {

    private Context context;
    private List<UserModel> userList;

    public SearchUserRecyclerAdapter(List<UserModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel model = userList.get(position);
        holder.usernameText.setText(model.getUsername());
        holder.lastNameText.setText(model.getLastName());

        // Indiquer si l'utilisateur est l'utilisateur actuel
        if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
            holder.usernameText.setText(model.getUsername() + " (Me)");
        }

        // Charger l'image de profil
        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                    } else {
                        holder.profilePic.setImageResource(R.drawable.ic_profile_picture); // Image par défaut
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            // Naviguer vers l'activité de chat
            Intent intent = new Intent(context, FragmentAmis.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.addFriendButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseUtil.getCurrentUser();
            if (currentUser != null) {
                // Vérifier dans la base de données si une invitation existe déjà
                DatabaseReference invitationsRef = FirebaseUtil.getInvitationsRef(model.getUserId()); // Référence Firebase pour les invitations
                invitationsRef.orderByChild("senderId").equalTo(FirebaseUtil.currentUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // Si l'invitation n'existe pas, l'ajouter
                                    DatabaseReference currentUserRef = FirebaseUtil.currentUserDetails();
                                    currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                UserModel senderModel = new UserModel();
                                                senderModel.setUsername(snapshot.child("username").getValue(String.class));
                                                senderModel.setLastName(snapshot.child("lastName").getValue(String.class));
                                                senderModel.setProfilePicUrl(String.valueOf(currentUser.getPhotoUrl()));

                                                // Envoyer l'invitation
                                                FirebaseUtil.sendFriendInvitation(model.getUserId(), senderModel);
                                                AndroidUtil.showToast(context, "Invitation envoyée à " + model.getUsername());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            AndroidUtil.showToast(context, "Erreur lors de la récupération des détails de l'utilisateur.");
                                        }
                                    });
                                } else {
                                    // Sinon, informer l'utilisateur que l'invitation existe déjà
                                    AndroidUtil.showToast(context, "Invitation déjà envoyée à " + model.getUsername());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                AndroidUtil.showToast(context, "Erreur lors de la vérification de l'invitation.");
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastNameText;
        ImageView profilePic;
        Button addFriendButton;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            lastNameText = itemView.findViewById(R.id.last_name_text);
            profilePic = itemView.findViewById(R.id.profile_pic);
            addFriendButton = itemView.findViewById(R.id.add_friend_button);
        }
    }
}
