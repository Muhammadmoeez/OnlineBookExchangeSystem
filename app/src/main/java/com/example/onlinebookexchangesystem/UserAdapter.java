package com.example.onlinebookexchangesystem;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirebaseRecyclerAdapter<ProductModel, UserAdapter.MyAllProductViewHolder>
{

    public UserAdapter(FirebaseRecyclerOptions<ProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder( UserAdapter.MyAllProductViewHolder myAllProductViewHolder, int position,  ProductModel userModel) {


        Picasso.get().load(userModel.getBookImage()).into(myAllProductViewHolder.imageViewProductImage);
        myAllProductViewHolder.textViewBookName.setText(userModel.getBookName());
        myAllProductViewHolder.textViewBookAuthorName.setText(userModel.getBookAuthor());


        myAllProductViewHolder.imageViewDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(myAllProductViewHolder.textViewBookName.getContext());

                builder.setTitle("Delete Product");
                builder.setMessage("Delete.....?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {



                        FirebaseDatabase.getInstance().getReference().child("Product")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getRef(position).getKey()).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("AllProduct")
                                .child(getRef(position).getKey()).removeValue();

                        dialogInterface.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });


                builder.show();
            }
        });

    }





    @Override
    public MyAllProductViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post,parent, false);
        return new MyAllProductViewHolder(view);
    }

    public static class MyAllProductViewHolder extends RecyclerView.ViewHolder{

        TextView textViewBookName, textViewBookAuthorName;

        ImageView imageViewProductImage, imageViewViewProduct, imageViewUpdateProduct, imageViewDeleteProduct;

        public MyAllProductViewHolder(View itemView) {
            super(itemView);


            imageViewProductImage = (ImageView) itemView.findViewById(R.id.userImageShowPost);

            textViewBookName = (TextView) itemView.findViewById(R.id.userBookNameShowPost);
            textViewBookAuthorName = (TextView) itemView.findViewById(R.id.userBookAuthorNameShowPost);


           // imageViewViewProduct = (ImageView) itemView.findViewById(R.id.adminViewShowPost);
          //  imageViewUpdateProduct = (ImageView) itemView.findViewById(R.id.adminEditShowPost);
            imageViewDeleteProduct = (ImageView) itemView.findViewById(R.id.adminDeleteShowPost);

        }
    }
}
