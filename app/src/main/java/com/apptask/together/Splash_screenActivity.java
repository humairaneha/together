package com.apptask.together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.apptask.together.databinding.ActivitySplashScreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class Splash_screenActivity extends AppCompatActivity {
       ActivitySplashScreenBinding binding;
       Animation animation;
       private static int SPLASH_SCREEN =4000;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySplashScreenBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        animation= AnimationUtils.loadAnimation(this,R.anim.animation);
        binding.logoImage.setAnimation(animation);
        auth = FirebaseAuth.getInstance();
     new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {

                     if(auth.getCurrentUser()==null){
                         startActivity(new Intent(Splash_screenActivity.this,MainActivity.class));
                         finish();
                     }
                     else{

                         startActivity(new Intent(Splash_screenActivity.this,HomeActivity.class));
                         finish();
                     }
                 }


     },SPLASH_SCREEN
     );


    }
}