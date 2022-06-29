package com.example.onlinebookexchangesystem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onlinebookexchangesystem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PublicDashboard extends AppCompatActivity implements OnMapReadyCallback {





    //Location

    Location currentLocation;
    private static final int REQUEST_CODE = 101;

    private Toolbar toolbar;

    ImageView regArrowBackPublicDashboard;

    FirebaseDatabase firebaseDatabaseUser;
    DatabaseReference databaseReferenceForUserData;

    private GoogleMap mMap;

    private Circle circle;



    private DatabaseReference mUsers;
    public FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_dashboard);
        regArrowBackPublicDashboard = (ImageView) findViewById(R.id.arrowBackPublicDashboard);
        regArrowBackPublicDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mUsers= FirebaseDatabase.getInstance().getReference().child("Users");




        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
    }

    private void fetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.findBookExchangers);
                    supportMapFragment.getMapAsync(PublicDashboard.this);
                }

            }
        });
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Double myLatitude = (Double) snapshot.child("UserLatitude").getValue();
                        Double myLongitude = (Double) snapshot.child("UserLongitude").getValue();
                        String myName = snapshot.child("UserName").getValue().toString();

                        int showFinalTimeCircle = 10000;

                       // LatLng latLng = new LatLng(myLatitude, myLongitude);
                        circle = googleMap.addCircle(new CircleOptions()
                                .center(new LatLng(currentLocation.getLatitude(),  currentLocation.getLongitude()))
                                .fillColor(R.color.white)
                                .radius(Double.parseDouble(String.valueOf(showFinalTimeCircle))));






                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(myName);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
                        googleMap.addMarker(markerOptions);
                        mMap = googleMap;

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });




        mUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()){

                    UsersModel user = s.getValue(UsersModel.class);

                    LatLng location = new LatLng(user.getUserLatitude(),user.getUserLongitude());
                    MarkerOptions options = new MarkerOptions();
                    options.title(user.getUserName());
                    options.snippet(user.getUserID());
                    options.position(location);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    mMap.addMarker(options);





                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            if (marker.getSnippet() == null){
                                Toast.makeText(PublicDashboard.this, "I am here "+marker.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                            else {

                                String clickedMarkerID = marker.getSnippet();
                                String clickedMarkerName = marker.getTitle();
                               // Toast.makeText(PublicDashboard.this, "Current Marker Title " + currentMarkerID, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PublicDashboard.this, ShowAllBooksOfCurrentUser.class);
                                intent.putExtra("ClickedID", clickedMarkerID);
                                intent.putExtra("ClickedName",clickedMarkerName );
                                startActivity(intent);

                            }
                            return false;
                        }
                    });



                }



            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });





    }






}