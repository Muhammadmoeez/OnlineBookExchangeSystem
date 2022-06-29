package com.example.onlinebookexchangesystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class ShowAllBooksOfCurrentUser extends AppCompatActivity {

    String tempUserClickedId, tempUserClickedName;

    ImageView regArrowBackHisBook;


    String parentCurrentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    RecyclerView.LayoutManager layoutManager;
   // Query hisBookReference;

    private RecyclerView hisRecyclerView;
    FirebaseRecyclerAdapter<ProductModel, MyHisBooksViewHolder> adapter;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    TextView textViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_books_of_current_user);

        textViewProfile = (TextView) findViewById(R.id.profile);

        regArrowBackHisBook = (ImageView) findViewById(R.id.arrowBackHisBook);
        regArrowBackHisBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        tempUserClickedId = intent.getStringExtra("ClickedID");
        tempUserClickedName = intent.getStringExtra("ClickedName");

        textViewProfile.setText(tempUserClickedName+"'s books");


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Product")
                .child(tempUserClickedId);


        hisRecyclerView = (RecyclerView) findViewById(R.id.hisAllBookRecyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        hisRecyclerView.setLayoutManager(layoutManager);

        showList();





    }

    private void showList() {

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(databaseReference, ProductModel.class).build();

        adapter = new FirebaseRecyclerAdapter<ProductModel, MyHisBooksViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ShowAllBooksOfCurrentUser.MyHisBooksViewHolder holder, int position, ProductModel productModel) {

                Picasso.get().load(productModel.getBookImage()).into(holder.imageViewHisProductImage);
                holder.textViewHisBookName.setText(productModel.getBookName());
                holder.textViewHisBookAuthorName.setText(productModel.getBookAuthor());

                holder.HisBookInterfaceClick(new HisBookOnClickShowFullPost() {
                    @Override
                    public void onClick(View view, boolean isLongPressed) {

                        Intent intent = new Intent(ShowAllBooksOfCurrentUser.this,RequiredBook.class);


                        intent.putExtra("UserOwnerNumber",productModel.getUserNumber());
                        intent.putExtra("BookImage", productModel.getBookImage());
                        intent.putExtra("BookName", productModel.getBookName());
                        intent.putExtra("BookAuthor",productModel.getBookAuthor());
                        ;

                        Double latitude = productModel.getBookLongitude();
                        Double longitude = productModel.getBookLatitude();


                        intent.putExtra("BookLongitude",longitude);
                        intent.putExtra("BookLatitude",latitude);

                        startActivity(intent);

                    }
                });
            }

            @NonNull

            @Override
            public MyHisBooksViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.public_post, viewGroup, false);
                return new MyHisBooksViewHolder(view);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        hisRecyclerView.setAdapter(adapter);
    }


    public static class MyHisBooksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewHisBookName, textViewHisBookAuthorName;

        ImageView imageViewHisProductImage;

        public HisBookOnClickShowFullPost hisBookOnClickShowFullPost;


        public MyHisBooksViewHolder(View itemView) {
            super(itemView);

            imageViewHisProductImage = (ImageView) itemView.findViewById(R.id.publicBookImageShowPost);
            textViewHisBookName = (TextView) itemView.findViewById(R.id.publicBookNameShowPost);
            textViewHisBookAuthorName = (TextView) itemView.findViewById(R.id.publicBookAuthorNameShowPost);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            hisBookOnClickShowFullPost.onClick(view, false);

        }
        public void HisBookInterfaceClick(HisBookOnClickShowFullPost hisBookOnClickShowFullPostView){

            this.hisBookOnClickShowFullPost = hisBookOnClickShowFullPostView;

        }
    }



}