package com.example.onlinebookexchangesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequiredBook extends AppCompatActivity implements OnMapReadyCallback {

    String currentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    int sum = 0;
    String tempBookOwnerNumber;
    String tempBookImage;
    String tempBookName;
    String tempBookAuthor;
    double tempBookLat;
    double tempBookLog;

    ImageView regArrowBackRequiredBook;

    private DatabaseReference databaseReference;

    List<String> carbonUids = new ArrayList<>();


    ImageView reqBookImage;
    TextView reqBookName, reqBookAuthorName;
    Button sendRequest;

    //Location
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private static final int REQUEST_CODE = 101;

    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_required_book);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RequiredBook.this);
        fetchLastLocation();



        reqBookImage = (ImageView) findViewById(R.id.reqImageBook);
        reqBookName = (TextView) findViewById(R.id.reqNameBook);
        reqBookAuthorName = (TextView) findViewById(R.id.reqWriterBook);

        regArrowBackRequiredBook = (ImageView) findViewById(R.id.arrowBackRequiredBook);
        regArrowBackRequiredBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("Product").child(currentId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    ProductModel productModel = s.getValue(ProductModel.class);

                    String bookName = productModel.getBookName();


                    Map<String, Object> map = (Map<String, Object>) s.getValue();
                    Object howManyBooks = map.get("BookCount");
                    int numberOfBooks = Integer.parseInt(String.valueOf(howManyBooks));
                    sum = sum + numberOfBooks;



                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        Intent intent = getIntent();
        tempBookOwnerNumber =intent.getStringExtra("UserOwnerNumber");
        tempBookImage =intent.getStringExtra("BookImage");
        tempBookName =intent.getStringExtra("BookName");
        tempBookAuthor =intent.getStringExtra("BookAuthor");
        tempBookLat =intent.getDoubleExtra("BookLongitude",0.0);
        tempBookLog =intent.getDoubleExtra("BookLatitude", 0.0);







        reqBookName.setText(tempBookName);
        reqBookAuthorName.setText(tempBookAuthor);
        Picasso.get().load(tempBookImage).into(reqBookImage);



        sendRequest = (Button) findViewById(R.id.bookRequest);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForBook();
            }
        });

//        message = "I have "+sum+" books, and I need your "+tempBookName+" book of author "+tempBookAuthor;
//        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    private void requestForBook() {

        message = "I have "+sum+" books, and I need your "+tempBookName+" book of author "+tempBookAuthor;
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();

        boolean installed = appInstalledOrNot("com.whatsapp");

        if(installed)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com//send?phone="+tempBookOwnerNumber+"&text="+message));
            startActivity(intent);




        }
        else
        {
            Toast.makeText(RequiredBook.this, "Whats-App is not Installed", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean appInstalledOrNot(String url) {

        PackageManager packageManager = getPackageManager();
        Boolean app_installed;
        try {

            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed;
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
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.reqBookLocationPoint);
                    supportMapFragment.getMapAsync(RequiredBook.this);
                }
            }
        });


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng = new LatLng(tempBookLat, tempBookLog);
        
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(tempBookOwnerNumber);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        googleMap.addMarker(markerOptions);

    }

}