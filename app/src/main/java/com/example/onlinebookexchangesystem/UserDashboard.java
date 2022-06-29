package com.example.onlinebookexchangesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDashboard extends AppCompatActivity {

    private Toolbar toolbar;

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    UserAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView myRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        toolbar = (Toolbar) findViewById(R.id.userDashboardToolBar);
        setSupportActionBar(toolbar);

        nav=(NavigationView)findViewById(R.id.navmenu);
        drawerLayout=(DrawerLayout)findViewById(R.id.userDashboardDrawerLayout);
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.addBook:

                        Intent intentAddTourismPointDetails = new Intent(UserDashboard.this, AddBook.class);
                        startActivity(intentAddTourismPointDetails);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.publicD:

                        Intent intentFindNearestPoint = new Intent(UserDashboard.this, PublicDashboard.class);
                        startActivity(intentFindNearestPoint);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(),"Logout User",Toast.LENGTH_LONG).show();
                        Intent intentLogoutUser = new Intent(UserDashboard.this, SignIn.class);
                        startActivity(intentLogoutUser);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Product")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRecyclerView = (RecyclerView) findViewById(R.id.myAllBookRecyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        myRecyclerView.setLayoutManager(layoutManager);

        FirebaseRecyclerOptions<ProductModel> options = new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(databaseReference, ProductModel.class).build();

        adapter = new UserAdapter(options);
        myRecyclerView.setAdapter(adapter);






    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.user_menu, menu);
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        int id = item.getItemId();
//
//        if (id== R.id.addBook)
//        {
//            Intent intent = new Intent(this,AddBook.class);
//            startActivity(intent);
//        }
//        else if (id == R.id.publicD){
//            Intent intent = new Intent(this,PublicDashboard.class);
//            startActivity(intent);
//        }
//
//        else if (id == R.id.logout)
//        {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent1 = new Intent(this,SignIn.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent1);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}