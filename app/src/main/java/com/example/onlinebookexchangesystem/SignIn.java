package com.example.onlinebookexchangesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    EditText logInEmail, logInPassword;
    Button logInBtn;
    TextView userRegistration;

    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        allowPermision();



        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        logInEmail = (EditText) findViewById(R.id.userLogInEmail);
        logInPassword = (EditText) findViewById(R.id.userLoginPassword);

        logInBtn = (Button) findViewById(R.id.userLogInBtn);
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logInUser();
            }
        });

        userRegistration = (TextView) findViewById(R.id.registration);
        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void allowPermision() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
    }

    private void logInUser() {

        final String savedLogInEmail = logInEmail.getText().toString();
        final String savedLogInPassword = logInPassword.getText().toString();

        if (TextUtils.isEmpty(savedLogInEmail)){
            logInEmail.requestFocus();
            logInEmail.setError("Enter Email");
        }
        else if (TextUtils.isEmpty(savedLogInPassword)){
            logInPassword.requestFocus();
            logInPassword.setError("Enter Password");
        }

        else {
            progressDialog.setTitle("LogIn");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(savedLogInEmail, savedLogInPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Query adminLogIn = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .orderByKey().equalTo(firebaseAuth.getCurrentUser().getUid());

                                adminLogIn.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()){

                                            Toast.makeText(SignIn.this, "Welcome User", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignIn.this, UserDashboard.class);
                                            startActivity(intent);
                                            progressDialog.dismiss();

                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignIn.this, "Not Admin ", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(SignIn.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {

                                progressDialog.dismiss();
                                Toast.makeText(SignIn.this, "Not Find Data", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }
}