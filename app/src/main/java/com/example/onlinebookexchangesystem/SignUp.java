package com.example.onlinebookexchangesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity implements OnMapReadyCallback {


    EditText regName, regEmail, regPassword, regConfirmPassword, regPhone;
    CheckBox checkBoxUser;
    RadioGroup radioGroupRegGander;
    int radioID;
    RadioButton radioButton;
    Button saveBtn;

    ImageView regArrowBackSignUp;



    //Location
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private static final int REQUEST_CODE = 101;





    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        regArrowBackSignUp = (ImageView) findViewById(R.id.arrowBackUserRegistration);
        regArrowBackSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SignUp.this);
        fetchLastLocation();

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();



        regName = (EditText) findViewById(R.id.userName);
        regEmail = (EditText) findViewById(R.id.userEmail);
        regPassword = (EditText) findViewById(R.id.userPassword);
        regConfirmPassword = (EditText) findViewById(R.id.userConfirmPassword);

        checkBoxUser = (CheckBox) findViewById(R.id.userCheckBox);
        checkBoxUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    regPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    regConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    regPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    regConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        regPhone = (EditText) findViewById(R.id.userPhone);

        //Radio Group
        radioGroupRegGander = (RadioGroup) findViewById(R.id.radioGroupGander);


        
        saveBtn = (Button) findViewById(R.id.logInWithEmail);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });
    }

    private void insertData() {


        radioID = radioGroupRegGander.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);



        final String registerName = regName.getText().toString();
        final String registerEmail = regEmail.getText().toString();
        final String registerPassword = regPassword.getText().toString();
        final String registerConfirmPassword = regConfirmPassword.getText().toString();
        final String registrationPhone = regPhone.getText().toString();
        final String registerGander = radioButton.getText().toString();


        if (TextUtils.isEmpty(registerName)){
            regName.requestFocus();
            regName.setError("Enter Name");
        }
        else if (TextUtils.isEmpty(registerEmail)){

            regEmail.requestFocus();
            regEmail.setError("Enter Email");
        }
        else if (TextUtils.isEmpty(registerPassword)){

            regPassword.requestFocus();
            regPassword.setError("Enter Password");
        }
        else if (TextUtils.isEmpty(registerConfirmPassword)){

            regConfirmPassword.requestFocus();
            regConfirmPassword.setError("Re-Enter Password");

        }
        else if (!registerPassword.matches(registerConfirmPassword)){
            regConfirmPassword.requestFocus();
            regConfirmPassword.setError("Your Password is not Matched");

        }
        else if (TextUtils.isEmpty(registrationPhone)){
            regPhone.requestFocus();
            regPhone.setError("Enter Phone Number");

        }
        else if (!registrationPhone.matches("\\+[0-9]{10,13}$")){

            regPhone.requestFocus();
            regPhone.setError("+923329882570");
        }
        else{

            progressDialog.setTitle("Registration");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                HashMap insertNewUserData = new HashMap();
                                insertNewUserData.put("UserName",registerName);
                                insertNewUserData.put("UserEmail",registerEmail);
                                insertNewUserData.put("UserPassword",registerPassword);
                                insertNewUserData.put("UserConfirmPassword",registerConfirmPassword);
                                insertNewUserData.put("UserGander",registerGander);
                                insertNewUserData.put("UserNumber",registrationPhone);
                                insertNewUserData.put("Role","User");
                                insertNewUserData.put("UserID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                insertNewUserData.put("UserLongitude",currentLocation.getLongitude());
                                insertNewUserData.put("UserLatitude",currentLocation.getLatitude());


                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(insertNewUserData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull  Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    Toast.makeText(SignUp.this, "User Registration", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(SignUp.this, SignIn.class);
                                                    startActivity(intent);
                                                }
                                                else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SignUp.this, "RealTime Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUp.this, "Auth Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



        }
    }




    private void fetchLastLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {


                if (location != null){

                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.userLocationPoint);
                    supportMapFragment.getMapAsync(SignUp.this);
                }
            }
        });


    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        googleMap.addMarker(markerOptions);

    }
}