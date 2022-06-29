package com.example.onlinebookexchangesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private FirebaseUser alReadyLoginUser;
    private NetworkInfo activeNetworkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        alReadyLoginUser = firebaseAuth.getCurrentUser();


        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        activeNetworkInfo =connectivityManager.getActiveNetworkInfo();
        hideNavigationBar();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (alReadyLoginUser == null){

                    Intent intent= new Intent(SplashScreen.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    updateUI();
                }

            }
        }, 3000);
    }



    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    private void hideNavigationBar() {


        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                        View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    private void updateUI() {

        if (alReadyLoginUser !=null && activeNetworkInfo != null){

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        String status =dataSnapshot.child("Role").getValue().toString();
                        if (status.equals("User")){

                            Intent intent = new Intent(SplashScreen.this, UserDashboard.class);
                            finish();
                            startActivity(intent);
                            Toast.makeText(SplashScreen.this, "Welcome User", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(SplashScreen.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



}