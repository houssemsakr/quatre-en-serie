package com.example.mynewapp;

import android.content.Context;
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

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private List<UserModel> friendList;

    public FriendAdapter(Context context, List<UserModel> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        UserModel userModel = friendList.get(position);

        holder.usernameText.setText(userModel.getUsername());
        holder.lastNameText.setText(userModel.getLastName());

        Glide.with(context)
                .load(userModel.getProfilePicUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.profilePic);

        holder.removeButton.setOnClickListener(v -> {
            removeFriend(userModel, position);
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    private void removeFriend(UserModel userModel, int position) {
        String friendIdToRemove = userModel.getUserId();
        String currentUserId = FirebaseUtil.currentUserId();
        FirebaseUtil.getDatabaseReference()
                .child("users").child(currentUserId).child("amis").child(friendIdToRemove)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        friendList.remove(position);
                        notifyItemRemoved(position);
                    }
                });
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView usernameText;
        TextView lastNameText;
        ImageButton removeButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            usernameText = itemView.findViewById(R.id.username_text);
            lastNameText = itemView.findViewById(R.id.last_name_text);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
