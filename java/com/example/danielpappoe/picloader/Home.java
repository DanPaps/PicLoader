package com.example.danielpappoe.picloader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.danielpappoe.picloader.chats.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import android.view.ContextMenu;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
//import io.smooch.ui.ConversationActivity;

import java.util.Collections;
import java.util.List;

import static android.R.id.list;
//import com.google.firebase.storage.StorageReference;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseRoot;
    private DatabaseReference mDatabseLike;
    private StorageReference mStorageRoot;
    private CircularImageView editPic;
    private TextView userName;
    private TextView emailAddress;
    private ImageView nav_background;
    private Uri resultUri = null;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private Boolean ProcessLike =false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //Firebase
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Kreative");
        mDatabaseRoot.keepSynced(true);
        mStorageRoot = FirebaseStorage.getInstance().getReference().child("Kreative");
        mDatabseLike = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Likes");
        mDatabseLike.keepSynced(true);
            mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(Home.this, login.class));
                    finish();
                }
            }
        };

        //Recycler view

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(Home.this);
       // manager.setOrientation(LinearLayoutManager.);
        //manager.setStackFromEnd(false);
        recyclerView.setLayoutManager(manager);

        ////when the image button is clicked


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ////////list the items in the navigation header
        navigationView.setNavigationItemSelectedListener(this);
        View navHeader = navigationView.getHeaderView(0);
        userName = (TextView) navHeader.findViewById(R.id.username);
        emailAddress = (TextView) navHeader.findViewById(R.id.email);
        nav_background = (ImageView) navHeader.findViewById(R.id.nav_background);
        editPic = (CircularImageView) navHeader.findViewById(R.id.imageProfile);


        loadNavHeader();
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    //LOAD CONTENT INTO THE NAVIGATION DRAWER
    private void loadNavHeader() {
        if (mAuth.getCurrentUser() != null) {
            final String user_uid = mAuth.getCurrentUser().getUid();
            final DatabaseReference mDatabaseImage = mDatabaseRoot.child("Clients").child(user_uid);
            // GET THE PROFILE ADDRESS AND LOAD IT INTO THE IMAGE VIEW USING PICASSO
            mDatabaseImage.child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        String key = dataSnapshot.getValue().toString();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
/*
                            Picasso.with(Home.this).load(Uri.parse(key)).placeholder(getDrawable(R.drawable.prof_2)).into(nav_background);
*/
                            Picasso.with(Home.this).load(Uri.parse(key)).placeholder(getDrawable(R.drawable.prof_2)).into(editPic);
                             Glide.with(Home.this).load(Uri.parse(key)).asBitmap().placeholder(getDrawable(R.drawable.prof_2)).into(nav_background);
                            // Glide.with(Home.this).load(Uri.parse(key)).asBitmap().placeholder(getDrawable(R.drawable.prof_2)).into(editPic);

                            //USING FRESCO TO LOAD IMAGES INTO THE IMAGE VIEW
                            //draweeView.setImageURI(imageUri);
                        } else {

                            Picasso.with(Home.this).load(Uri.parse(key)).into(nav_background);

                            //Glide.with(Home.this).load(Uri.parse(key)).asBitmap().into(nav_background);

                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    String error = databaseError.getDetails();
                    Log.d(Home.class.getSimpleName(), error);        //LOG THE ERROR
                }
            });
/*

                if (mAuth.getCurrentUser() != null) {
                    user_uid = mAuth.getCurrentUser().getUid();
                    mDatabaseImage = mDatabaseRoot.child(user_uid);
                    //GET THE PROFILE ADDRESS AND LOAD IT INTO THE IMAGE VIEW USING PICASSO
                    mDatabaseImage.child("Profile").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String key = dataSnapshot.getValue().toString();
                            //Glide.with(editProfile.this).load(Uri.parse(key)).placeholder(R.drawable.prof_2).into(imageView);
                            editPic = (CircularImageView) findViewById(R.id.imageProfile);
                            Picasso.with(Home.this).load(Uri.parse(key)).placeholder(R.drawable.prof_2).into(editPic);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            String error = databaseError.getDetails();
                            Toast.makeText(Home.this, "Error " + error + "occurred.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
*/


            mDatabaseImage.child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getValue().toString();
                    // name, email
                    userName.setText(key);
                    emailAddress.setText(mAuth.getCurrentUser().getEmail());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    String error = databaseError.getDetails();
                }
            });
            editPic.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            final CharSequence[] items = {"View photo", "Choose from Gallery", "Take a photo", "Delete"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                            //  builder.setTitle("Share Appliction");
                            builder.setTitle("Change Profile Picture");
                            builder.setIcon(R.drawable.edituser);
                            builder.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    if (item == 0) {

                                    } else if (item == 1) {
                                        Toast.makeText(Home.this, "there is no action yet", Toast.LENGTH_SHORT).show();
                                    } else if (item == 2) {

                                    } else if (item == 3) {

                                    }
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();


                        }
                    }
            );




   /*             editPic.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                registerForContextMenu(editPic);
                                                   }

                                //startActivity(new Intent(Home.this, editProfile.class));
                            }
                        }
                );*/

        }

    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.status) {
            startActivity(new Intent(Home.this, status.class));
        }
        if (id==R.id.Post){
            startActivity(new Intent(Home.this, Post_To_Blog.class));
        }
        if (id == R.id.meno) {
            startActivity(new Intent(Home.this, Memo.class));
            return true;
        }
        if (id == R.id.rate) {
            startActivity(new Intent(Home.this, Rate.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            // Handle the profile action
            //Intent i =new Intent(Home.this,Profile.class);
            //startActivity(i);
            startActivity(new Intent(Home.this, MyProfile.class));

        } else if (id == R.id.help) {
            //ConversationActivity.show(this);

        } else if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            builder.setMessage("Are you sure you want to log out of Pics Upload?");
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mAuth.signOut();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        }
        else if (id == R.id.contacts) {


        }else if (id == R.id.chats) {
            startActivity(new Intent(Home.this, MainActivity.class));

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //fire base recycler view

        FirebaseRecyclerAdapter<Blog, BlogViewHolder_Home> adapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder_Home>
                (
                        Blog.class,
                        R.layout.blog_viewholder__home,
                        BlogViewHolder_Home.class,
                        mDatabaseRoot.child("Blog")

                ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder_Home viewHolder, final Blog model, int position) {
                //Populate the View into the recyclerView
                final String post_key= getRef(position).getKey();

                viewHolder.setEmail(model.getEmail());
                viewHolder.setDescription(model.getDesc());
                viewHolder.setImage(model.getImage(), getApplicationContext());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setProfile(model.getProfile(), getApplicationContext());
                viewHolder.set_likeButton(post_key);


                //SETTING A CLICK LISTENER FOR THE PROFILE IMAGE
                viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //THIS DISPLAYS THE PROFILE OF THE SENDER TO THE ALERT DIALOG BOX


                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        ImageView imageView = new ImageView(Home.this);
                        // TextView title =new TextView(Home.this);
                        //title.setText(viewHolder.blog_username_blog.getText());



                        imageView.setImageDrawable(viewHolder.post_image.getDrawable());
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setAdjustViewBounds(true);
                        //  builder.setTitle(viewHolder.setUsername(model.getUsername());
                        builder.setView(imageView);
                        // builder.setTitle((CharSequence) title);
                        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();



                    }
                });

                //Setting a click listener for the blog menu

                viewHolder.blogMenu.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final CharSequence[] items = {"View Pic", "Call User", "Rate", "Share on WhatsApp"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                                //  builder.setTitle("Share Appliction");
                                builder.setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        if (item == 0) {

                                        } else if (item == 1) {

                                        } else if (item == 2) {

                                        } else if (item == 3) {

                                        }
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();

                            }
                        }
                );

                //SETTING A CLICK LISTENER FOR THE LIKE BUTTON
                viewHolder.likeButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ProcessLike = true;
                                mDatabseLike.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (ProcessLike) {
                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                                mDatabseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                ProcessLike = false;


                                            } else {
                                                mDatabseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("random value");
                                                ProcessLike = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled (DatabaseError databaseError){

                                    }

                                });


                            }
                        }
                );

                //SETTING CLICK LISTENER FOR THE POST IMAGE

                viewHolder.post_blog_image.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Toast.makeText(Home.this,post_key,Toast.LENGTH_LONG).show();
                                Intent Blog_single =new Intent(Home.this, BlogSingle.class);
                                Blog_single.putExtra("blog_id",post_key);
                                startActivity(Blog_single);

                            }
                        }
                );




            }

        };

        recyclerView.setAdapter(adapter);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



}
