package com.example.danielpappoe.picloader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogViewHolder_Home  extends RecyclerView.ViewHolder{
    private View mView;
    public CircleImageView post_image;
    public ImageView post_blog_image;
    public ImageView blogMenu,comment,bookmark;
    public TextView blog_email_blog;
    public TextView blog_username_blog;
    public TextView blog_desc_blog;
    public ImageView likeButton;
    public ImageView unlikeButton;

    DatabaseReference mDatabaseLike;
    FirebaseAuth mAuth;

    public BlogViewHolder_Home(View itemView) {
        super(itemView);
        mView = itemView;
        blog_desc_blog =(TextView)mView.findViewById(R.id.blog_desc_blog);
        blog_username_blog =(TextView)mView.findViewById(R.id.blog_user_blog);
        blog_email_blog =(TextView)mView.findViewById(R.id.blog_email_blog);

        post_image = (CircleImageView) mView.findViewById(R.id.blog_sender_profile);
        post_blog_image = (ImageView) mView.findViewById(R.id.blog_image_home);
        blogMenu =(ImageView)mView.findViewById(R.id.blogMenu);
        comment =(ImageView)mView.findViewById(R.id.comment_blog);
        bookmark =(ImageView)mView.findViewById(R.id.bookmark);
        likeButton =(ImageView)mView.findViewById(R.id.unlike);
        unlikeButton =(ImageView)mView.findViewById(R.id.like);

        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mAuth =FirebaseAuth.getInstance();
        mDatabaseLike.keepSynced(true);
    }

    public void set_likeButton(final String post_key){
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //  likeButton.setVisibility(View.INVISIBLE);
                //  unlikeButton.setVisibility(View.VISIBLE);
          /*      if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                    unlikeButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                }else{
                    likeButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void setImage(final String image, final Context ctx) {
        final ImageView imageView = (ImageView) mView.findViewById(R.id.blog_image_home);
        Glide.with(ctx).load(image).asBitmap().placeholder(R.drawable.ic_filter_hdr_black_24dp).into(imageView);
       /* Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_filter_hdr_black_24dp).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(ctx).load(image).placeholder(R.drawable.ic_filter_hdr_black_24dp).into(imageView);
            }
        });*/
    }


    public void setUsername(String username) {
        TextView post_username = (TextView) mView.findViewById(R.id.blog_user_blog);
        post_username.setText(username);
    }

    public void setProfile(String profile, Context ctx) {
        ImageView imageView1 = (ImageView) mView.findViewById(R.id.blog_sender_profile);
        Glide.with(ctx).load(profile).asBitmap().placeholder(R.drawable.prof_2).into(imageView1);
    }
    public  void setEmail(String email){
        TextView blog_email_blog =(TextView)mView.findViewById(R.id.blog_email_blog);
        blog_email_blog.setText(email);
    }
    public void setDescription(String description){
        TextView blog_desc_blog=(TextView)mView.findViewById(R.id.blog_desc_blog);
        blog_desc_blog.setText(description);
    }
}

