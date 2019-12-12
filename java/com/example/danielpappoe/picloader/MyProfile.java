package com.example.danielpappoe.picloader;


import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import static com.example.danielpappoe.picloader.R.id.image_View;



public class MyProfile extends AppCompatActivity {
    public Button editProfile;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRoot;
    private StorageReference mStorageRoot;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        final Toolbar layout1=(Toolbar)findViewById(R.id.toolbar);


        mAuth = FirebaseAuth.getInstance();
        mStorageRoot = FirebaseStorage.getInstance().getReference().child("Kreative").child("Clients");
        mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Clients");
        mDatabaseRoot.keepSynced(true);


        if (mAuth.getCurrentUser() != null) {
            String user_uid = mAuth.getCurrentUser().getUid();
            DatabaseReference mDatabaseImage = mDatabaseRoot.child(user_uid);
            //GET THE PROFILE ADDRESS AND LOAD IT INTO THE IMAGE VIEW USING PICASSO
            mDatabaseImage.child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getValue().toString();
                    imageView = (ImageView) findViewById(R.id.image_View);
                   // Picasso.with(MyProfile.this).load(Uri.parse(key)).placeholder(R.drawable.prof_2).into(imageView);
                    //  Picasso.with(MyProfile.this).load(Uri.parse(key)).placeholder(R.drawable.prof_2).into(layout1.setBackground(Uri.parse(key)));
                    // layout1.setBackground(android.net.U);
                    Glide.with(MyProfile.this).load(Uri.parse(key)).asBitmap().placeholder(R.drawable.prof_2).into(imageView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    String error = databaseError.getDetails();
                    Toast.makeText(MyProfile.this, "Error " + error + "occurred.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //FIREBASE
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Clients");
        mDatabaseRoot.keepSynced(true);
        // GET THE USERNAME AND EMAIL ADDRESS AND LOAD IT INTO THE THE CARD VIEW
        if (mAuth.getCurrentUser() != null){
            String mail = mAuth.getCurrentUser().getEmail();
            String user_uid = mAuth.getCurrentUser().getUid();
            TextView textView = (TextView) findViewById(R.id.user_id);
            textView.setText(user_uid);
            TextView textView1 = (TextView) findViewById(R.id.user_email);
            textView1.setText(mail);
            DatabaseReference mDatabaseImage = mDatabaseRoot.child(user_uid);

            //UPDATES THE USERNAME
            mDatabaseImage.child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TextView userName=(TextView)findViewById(R.id.user_name);
                    String username = dataSnapshot.getValue().toString();
                    //  To set the format of the user name do below
                  /*layout.setTitle(String.format("%s",username));*/
                    layout.setTitle(username);
                    userName.setText(username);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //UPDATES THE STATUS
        if (mAuth.getCurrentUser() != null) {
            String user_uid = mAuth.getCurrentUser().getUid();
            DatabaseReference mDatabaseImage = mDatabaseRoot.child(user_uid);
            //THERE IS NO NEED FOR THE TRY AND CATCH PHRASE
            try {
                mDatabaseImage.child("status").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            String status = dataSnapshot.getValue().toString();
                            TextView status1 = (TextView) findViewById(R.id.status);
                            status1.setText(status);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }






        editProfile = (Button) findViewById(R.id.editprofile);
        editProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyProfile.this, editProfile.class));
                    }
                }
        );


    }

}
