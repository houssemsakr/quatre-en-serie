package com.example.mynewapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynewapp.model.UserModel;
import com.example.mynewapp.utils.FirebaseUtil;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder> {

    private Context context;
    private List<UserModel> invitationList;

    public InvitationAdapter(Context context, List<UserModel> invitationList) {
        this.context = context;
        this.invitationList = invitationList;
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invitation, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        UserModel userModel = invitationList.get(position);

        holder.usernameText.setText(userModel.getUsername());
        holder.lastNameText.setText(userModel.getLastName());

        Glide.with(context)
                .load(userModel.getProfilePicUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profilePic);

        // Accept invitation logic
        holder.acceptButton.setOnClickListener(v -> {
            acceptInvitation(userModel);
        });

        // Remove invitation logic
        holder.removeButton.setOnClickListener(v -> {
            removeInvitation(userModel, position);
        });
    }

    @Override
    public int getItemCount() {
        return invitationList.size();
    }

    private void acceptInvitation(UserModel userModel) {
        String currentUserId = FirebaseUtil.currentUserId();
        if (currentUserId != null) {
            DatabaseReference friendsRef = FirebaseUtil.getDatabaseReference()
                    .child("users").child(currentUserId).child("amis").push();

            String amiId = userModel.getUserId();
            Log.d("InvitationAdapter", "Accepting invitation from UserId: " + amiId);

            // Ajouter l'utilisateur à la liste des amis
            friendsRef.child("amiId").setValue(amiId);
            friendsRef.child("username").setValue(userModel.getUsername());
            friendsRef.child("lastName").setValue(userModel.getLastName());
            friendsRef.child("profilePicUrl").setValue(userModel.getProfilePicUrl());

            // Supprimer l'invitation après l'acceptation
            removeInvitation(userModel, invitationList.indexOf(userModel));
        }
    }

    private void removeInvitation(UserModel userModel, int position) {
        String userIdToRemove = userModel.getUserId();
        Log.d("InvitationAdapter", "Tentative de suppression de l'invitation pour UserId: " + userIdToRemove);

        // Référence à l'invitation dans la base de données
        DatabaseReference userInvitationRef = FirebaseUtil.getDatabaseReference()
                .child("users").child(FirebaseUtil.currentUserId()).child("invitations").child(userIdToRemove);

        // Essayer de supprimer l'invitation
        userInvitationRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("InvitationAdapter", "Invitation supprimée avec succès pour UserId: " + userIdToRemove);
                // Supprimer l'invitation de la liste locale
                invitationList.remove(position);
                notifyItemRemoved(position); // Notifier que l'élément a été supprimé
            } else {
                Log.e("InvitationAdapter", "Échec de la suppression de l'invitation: " + task.getException());
            }
        });
    }

    public static class InvitationViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView usernameText;
        TextView lastNameText;
        ImageButton acceptButton;
        ImageButton removeButton;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            usernameText = itemView.findViewById(R.id.username_text);
            lastNameText = itemView.findViewById(R.id.last_name_text);
            acceptButton = itemView.findViewById(R.id.accept_button);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
