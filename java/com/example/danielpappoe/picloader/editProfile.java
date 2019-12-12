package com.example.danielpappoe.picloader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Key;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class editProfile extends AppCompatActivity {
    private CircularImageView imageView;
    private EditText username;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRoot;
    private ProgressDialog progressDialog;
    private StorageReference mStorageRoot;
    private RelativeLayout layout;

    private Uri resultUri = null;
    private String User_uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        imageView = (CircularImageView) findViewById(R.id.imageProfile);
        final ImageView editStatus = (ImageView) findViewById(R.id.edit_status);
        final ImageView editDone =(ImageView)findViewById(R.id.edit_done);
        final TextView Status = (TextView) findViewById(R.id.status);
        final EditText edt_status = (EditText) findViewById(R.id.editText_status);
        username = (EditText) findViewById(R.id.username);
        layout = (RelativeLayout) findViewById(R.id.content_edit_profile);
        Button updateProfile = (Button) findViewById(R.id.updateprofile);
       /* final ImageView editDone = (ImageView) findViewById(R.id.edit_done);
        final ImageView editUsername = (ImageView) findViewById(R.id.edit_username);
        final TextView user_status = (TextView) findViewById(R.id.status);
        final EditText edt_username = (EditText) findViewById(R.id.editText_status);


*/





        progressDialog = new ProgressDialog(editProfile.this);
        progressDialog.setMessage("updating your details");
        progressDialog.setCanceledOnTouchOutside(false);





        mAuth = FirebaseAuth.getInstance();


        mStorageRoot = FirebaseStorage.getInstance().getReference().child("Kreative").child("Clients");
        mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Clients");

        mDatabaseRoot.keepSynced(true);

        //Push username to replace the edit text space
        try {
            mDatabaseRoot.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String status = dataSnapshot.getValue().toString();
                        username.setText(status);

                    }else {
                        if(dataSnapshot != null){
                            String value = null;
                            //   Toast.makeText(editProfile.this, "No status found", Toast.LENGTH_SHORT).show();
                            try {
                                value = dataSnapshot.getValue().toString();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            username.setText(value);


                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }




        //UPDATES THE STATUS
        if (mAuth.getCurrentUser() != null) {
            String user_uid = mAuth.getCurrentUser().getUid();
            DatabaseReference mDatabaseImage = mDatabaseRoot.child(user_uid);
            //THERE IS NO NEED FOR THE TRY AND CATCH PHRASE
            //i used it because there was no child created for STATUS
            try {
                mDatabaseImage.child("status").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            String status = dataSnapshot.getValue().toString();
                            Status.setText(status);
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

        //UPDATES THE STATUS
        if (mAuth.getCurrentUser() != null) {
            String user_uid = mAuth.getCurrentUser().getUid();
            DatabaseReference mDatabaseImage = mDatabaseRoot.child(user_uid);
            mDatabaseImage.child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getValue().toString();
                    // name, email
                    username.setText(key);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    String error = databaseError.getDetails();
                }
            });}
        //Update the ImageVIew box

        if (mAuth.getCurrentUser() != null) {
            String user_uid = mAuth.getCurrentUser().getUid();
            DatabaseReference mDatabaseImage = mDatabaseRoot.child(user_uid);
            //GET THE PROFILE ADDRESS AND LOAD IT INTO THE IMAGE VIEW USING PICASSO
            mDatabaseImage.child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getValue().toString();
                    imageView = (CircularImageView) findViewById(R.id.imageProfile);
                    //Picasso.with(editProfile.this).load(Uri.parse(key)).placeholder(R.drawable.prof_2).into(imageView);
                    // Glide.with(editProfile.this).load(Uri.parse(key)).placeholder(getDrawable(R.drawable.prof_2)).into(imageView);
                    //Glide.with(editProfile.this).load(Uri.parse(key)).placeholder(getDrawable(R.drawable.prof_2)).into(imageView);
//                    Glide.with(editProfile.this).load(Uri.parse(key)).asBitmap().into(imageView);
                    Glide.with(editProfile.this).load(Uri.parse(key)).asBitmap().placeholder(R.drawable.prof_2).into(imageView);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    String error = databaseError.getDetails();
                    Toast.makeText(editProfile.this, "Error " + error + "occurred.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        //LISTENERS for the update status card view

        editStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Status.setVisibility(View.INVISIBLE);
                edt_status.setVisibility(View.VISIBLE);
                editStatus.setVisibility(View.INVISIBLE);
                editDone.setVisibility(View.VISIBLE);
            }
        });

        editDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Status.setVisibility(View.VISIBLE);
                edt_status.setVisibility(View.INVISIBLE);

                if (!TextUtils.isEmpty(edt_status.getText().toString())){
                    if (mAuth.getCurrentUser() != null){
                        final String user_id = mAuth.getCurrentUser().getUid();
                        String user_email = mAuth.getCurrentUser().getEmail();
                        final ProgressDialog builder = new ProgressDialog(editProfile.this);
                        builder.setMessage("Updating username for " + user_email + "...");
                        builder.show();
                        DatabaseReference curr_user = mDatabaseRoot.child(user_id).child("status");
                        curr_user.setValue(edt_status.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                builder.dismiss();
                                editStatus.setVisibility(View.VISIBLE);
                                editDone.setVisibility(View.INVISIBLE);
                                DatabaseReference mDatabaseImage = mDatabaseRoot.child(user_id);
                                mDatabaseImage.child("status").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String value = dataSnapshot.getValue().toString();
                                        Status.setText(value);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                builder.dismiss();
                                Toast.makeText(editProfile.this, "Process failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else{
                    Status.setVisibility(View.VISIBLE);
                    edt_status.setVisibility(View.INVISIBLE);
                    editStatus.setVisibility(View.VISIBLE);
                    editDone.setVisibility(View.INVISIBLE);
                }
            }
        });



        /////
        // TODO: 5/18/2017 i have to make the username in the database display in the editbox




        updateProfile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ////////upload the pic and move to the "my profile intent
                        if (!TextUtils.isEmpty(username.getText().toString())) {

                            try { final String user_uid = mAuth.getCurrentUser().getUid();
                                if (imageView != null) {

                                    DatabaseReference current_user_db = mDatabaseRoot.child(user_uid);
                                    progressDialog.show();
                                    current_user_db.child("username").setValue(username.getText().toString()).addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(editProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(editProfile.this, MyProfile.class));
                                                    finish();
                                                }
                                            }
                                    ).addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(editProfile.this, "Process failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    );
                                }
                                ///Push the image to replace the default value of the profile picture
                                StorageReference mProfile = mStorageRoot.child(user_uid).child(resultUri.getLastPathSegment());
                                mProfile.putFile(resultUri).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Snackbar.make(layout, "Profile update failed", Snackbar.LENGTH_LONG).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        @SuppressLint("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                                        DatabaseReference newPost = mDatabaseRoot.child(user_uid);
                                        newPost.child("profile").setValue(downloadUri.toString());
                                        //progressDialog.dismiss();

                                    }
                                });


                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                //Toast.makeText(editProfile.this, "Select an image first", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Snackbar.make(layout, "Setup account to continue", Snackbar.LENGTH_LONG).show();
                        }

                        //           startActivity(new Intent(editProfile.this, MyProfile.class));
                    }
                }
        );


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ShareCompat.IntentBuilder.from(editProfile.this).setType("image/*").startChooser();

//                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAutoZoomEnabled(true)
                    .setBackgroundColor(R.color.colorAccent)
                    .setMaxZoom(4)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setGuidelinesColor(R.color.colorPrimaryDark)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        //Crop Image using the Image cropper tool
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                String error_msg = error.getMessage();
                Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show();
            }
        }





    }


}
