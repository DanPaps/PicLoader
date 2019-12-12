package com.example.danielpappoe.picloader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingle extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String post_key =null;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseDelete;
    private ImageView  BlogImage;
    private TextView BlogDesc;
    private TextView BlogUsername;
    private TextView BlogPhoneNumber;
    private ImageView BlogProfilePic;
    private Button Delete_Button;
    private ProgressDialog progressDialog;

    private Boolean ProcessDelete =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting From blog");
        progressDialog.setCanceledOnTouchOutside(false);
        //Toast.makeText(BlogSingle.this,post_key, Toast.LENGTH_LONG).show();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Kreative");
        mAuth = FirebaseAuth.getInstance();
        mDatabase.keepSynced(true);

        mDatabaseDelete =FirebaseDatabase.getInstance().getReference().child("Kreative").child("Blog");
        mDatabaseDelete.keepSynced(true);

        post_key =getIntent().getExtras().getString("blog_id");

        BlogImage =(ImageView)findViewById(R.id.blog_image);
        BlogDesc =(TextView) findViewById(R.id.blog_desc_blog2);
        BlogUsername =(TextView) findViewById(R.id.blog_user_blog);
        BlogPhoneNumber =(TextView) findViewById(R.id.blog_phone_blog);
        BlogProfilePic =(ImageView)findViewById(R.id.blog_sender_profile);
        Delete_Button =(Button) findViewById(R.id.delete_from_blog);

        mDatabase.child("Blog").child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Post_Phone =(String) dataSnapshot.child("phone").getValue();
                String Post_Desc =(String) dataSnapshot.child("desc").getValue();
                String Post_Image =(String) dataSnapshot.child("image").getValue();
                String Post_Uid =(String) dataSnapshot.child("uid").getValue();
                String Post_Profile =(String) dataSnapshot.child("profile").getValue();
                String Post_Username =(String) dataSnapshot.child("username").getValue();

                BlogDesc.setText(Post_Desc);
                BlogUsername.setText(Post_Username);
                BlogPhoneNumber.setText(Post_Phone);

                Glide.with(BlogSingle.this).load(Post_Image).asBitmap().into(BlogImage);
                Glide.with(BlogSingle.this).load(Post_Profile).asBitmap().into(BlogProfilePic);

                if(mAuth.getCurrentUser().getUid().equals(Post_Uid)){
                    Delete_Button.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Delete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // progressDialog.show();
                //mDatabase.child("Blog").child(post_key).removeValue();

                mDatabaseDelete.child(post_key).removeValue();

                Intent mainIntent = new Intent(BlogSingle.this,Home.class);
                startActivity(mainIntent);
                //  progressDialog.dismiss();
            }
        });




    }
}

