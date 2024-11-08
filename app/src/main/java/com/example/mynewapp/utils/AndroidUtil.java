package com.example.mynewapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mynewapp.model.UserModel;

public class AndroidUtil {

    // Affiche un Toast avec un message donné
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // Passe les informations de l'utilisateur dans l'intent sans le champ "phone"
    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("lastName", model.getLastName());
        intent.putExtra("country", model.getCountry());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("fcmToken", model.getFcmToken());
        intent.putExtra("profilePicUrl", model.getProfilePicUrl());
    }

    // Récupère les informations de l'utilisateur depuis l'intent
    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setLastName(intent.getStringExtra("lastName"));
        userModel.setCountry(intent.getStringExtra("country"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        userModel.setProfilePicUrl(intent.getStringExtra("profilePicUrl"));
        return userModel;
    }

    // Charge et affiche l'image de profil à partir d'une URL ou URI
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    // Surcharge pour charger l'image à partir d'une URL
    public static void setProfilePicFromUrl(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }
}