package com.example.onlinebookexchangesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class AddBook extends AppCompatActivity implements OnMapReadyCallback {

    ImageView regArrowBackAddBook;
    private ProgressDialog progressDialog;

    //DataBaseDeclaration
    StorageReference storageReference;
    private StorageTask uploadTask;

    DatabaseReference addBookDatabaseReference;

    Double saveUserLongitude;
    Double saveUserLatitude;

    FirebaseDatabase firebaseDatabaseUser;
    DatabaseReference databaseReferenceForUserData;

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    String userIdForBook;

    EditText regBookName, regBookAuthor;
    ImageView regBookImage;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    Uri selectImageUri;
    Button regBookBtn;

    //Location
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);




        progressDialog = new ProgressDialog(this);
        firebaseDatabaseUser = FirebaseDatabase.getInstance();

        userIdForBook = FirebaseAuth.getInstance().getCurrentUser().getUid();

        regBookImage = (ImageView) findViewById(R.id.bookImage);
        regBookName = (EditText) findViewById(R.id.bookName);
        regBookAuthor  = (EditText) findViewById(R.id.bookWriter);
        regBookBtn = (Button) findViewById(R.id.bookRegistration);

        regBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                insertData();
            }
        });

        regBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();

            }
        });

        regArrowBackAddBook = (ImageView) findViewById(R.id.arrowBackAddBook);
        regArrowBackAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AddBook.this);
        fetchLastLocation();
    }

    private void insertData() {

        final String saveBookName = regBookName.getText().toString();
        final String saveBookAuthor = regBookAuthor.getText().toString();

        if (TextUtils.isEmpty(saveBookName)){
            regBookName.requestFocus();
            regBookName.setError("Enter Name");
        }
        else if (TextUtils.isEmpty(saveBookAuthor)){
            regBookAuthor.requestFocus();
            regBookAuthor.setError("Enter Author Name");
        }
        else {


            progressDialog.setTitle("Insert Data");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            storageReference = FirebaseStorage.getInstance().getReference().child("BookImages");
            final StorageReference myRef = storageReference.child(System.currentTimeMillis() + "." + getExtension(selectImageUri));

            uploadTask = myRef.putFile(selectImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final String bookImageURI = String.valueOf(uri);



                                  //   create Custom Id's for save child
                                    addBookDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Product");
                                    String id = addBookDatabaseReference.push().getKey();




                                    databaseReferenceForUserData = firebaseDatabaseUser.getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    

                                    databaseReferenceForUserData.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange( DataSnapshot snapshot) {

                                            String saveUserNumber = snapshot.child("UserNumber").getValue(String.class);

                                            saveUserLongitude = (Double) snapshot.child("UserLongitude").getValue();
                                            saveUserLatitude = (Double) snapshot.child("UserLatitude").getValue();



                                            final HashMap insertBook = new HashMap();
                                            insertBook.put("BookId",id);
                                            insertBook.put("UserId",userIdForBook);
                                            insertBook.put("UserNumber",saveUserNumber);
                                            insertBook.put("BookImage",bookImageURI);
                                            insertBook.put("BookName", saveBookName);
                                            insertBook.put("BookAuthor", saveBookAuthor);
                                            insertBook.put("BookLongitude",saveUserLongitude);
                                            insertBook.put("BookLatitude",saveUserLatitude);
                                            insertBook.put("BookCount","1");

                                            FirebaseDatabase.getInstance().getReference().child("Product")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(id)
                                                    .setValue(insertBook)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(Task<Void> task) {


                                                            if (task.isSuccessful()){


                                                                FirebaseDatabase.getInstance().getReference().child("AllProduct")
                                                                        .child(id)
                                                                        .setValue(insertBook)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful()){
                                                                                    Toast.makeText(AddBook.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(AddBook.this, UserDashboard.class);
                                                                                    startActivity(intent);
                                                                                }
                                                                                else {
                                                                                    Toast.makeText(AddBook.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
//
                                                            }

                                                            else {

                                                                Toast.makeText(AddBook.this, ""+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }


                                                        }
                                                    });

                                        }

                                        @Override
                                        public void onCancelled( DatabaseError error) {

                                        }
                                    });





                                }
                            });


                        }
                    });

        }




    }

    private void SelectImage() {


        //        final CharSequence[] items={"Camera", "Gallery", "Cancel"};
        final CharSequence[] items={ "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddBook.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {



                if (items[i].equals("Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);

                }
                else if (items[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }
            }
        });

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // openCamera();
                } else {
                    Toast.makeText(this, "Permission Denied....", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        if (resultCode == Activity.RESULT_OK){
            regBookImage.setImageURI(selectImageUri);

            if (requestCode == REQUEST_CAMERA){


            }
            else if (requestCode == SELECT_FILE){
                selectImageUri = data.getData();
                regBookImage.setImageURI(selectImageUri);

            }
        }
    }


    private  String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
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
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.bookLocationPoint);
                    supportMapFragment.getMapAsync(AddBook.this);
                }
            }
        });


    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        Toast.makeText(this, ""+latLng, Toast.LENGTH_SHORT).show();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        googleMap.addMarker(markerOptions);

    }
}