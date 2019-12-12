package com.example.danielpappoe.picloader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Post_To_Blog extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRoot;
    private StorageReference mStorageRoot;
    private static final int GALLERY_INTENT = 1;
    private DatabaseReference mDatabaseUser;
    private FirebaseUser mCurrentUser;

    //VIEW ITEMS
    private ImageView imageView;
    private TextInputEditText  post_desc;
    private ProgressDialog progressDialog;
    private EditText phoneNumber;
    private RelativeLayout relativeLayout;
    private Uri resultUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__to__blog);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Kreative");
        mDatabaseRoot.keepSynced(true);
        mStorageRoot = FirebaseStorage.getInstance().getReference().child("Kreative");
        mCurrentUser = mAuth.getCurrentUser();

        //Items
        imageView = (ImageView) findViewById(R.id.blog_image_blog);
        post_desc = (TextInputEditText) findViewById(R.id.blog_desc_blog);
        phoneNumber =(EditText) findViewById(R.id.PhoneNumber);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_post__to__blog);


        Button btnUpload = (Button) findViewById(R.id.upload);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading to blog");
        progressDialog.setCanceledOnTouchOutside(false);


        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Clients").child(mCurrentUser.getUid());

        mDatabaseUser.keepSynced(true);

        //Listener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_INTENT);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });


    }

    //////////////////////////////////////////////////////////////////////////////////
    //startPosting
    private void startPosting() {
        final String phone_num = phoneNumber.getText().toString().trim();
        final String desc_val = post_desc.getText().toString().trim();

        //CHECKS FOR THE CONTENT OF THE TITLE AND THE DESC

        if(!TextUtils.isEmpty(phone_num) && (!TextUtils.isEmpty(desc_val))){
            progressDialog.show();
            StorageReference filepath = mStorageRoot.child("Blog").child(resultUri.getLastPathSegment());
            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final DatabaseReference newPost = mDatabaseRoot.child("Blog").push();
                    final Uri downloadUri = taskSnapshot.getDownloadUrl();




                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("phone").setValue(phone_num);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("profile").setValue(dataSnapshot.child("profile").getValue().toString());
                            newPost.child("email").setValue(mCurrentUser.getEmail());
                            newPost.child("image").setValue(downloadUri.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(Post_To_Blog.this,Home.class));
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    progressDialog.dismiss();


                }
            });
        }

      /*  if (!TextUtils.isEmpty(phone_num) && (!TextUtils.isEmpty(desc_val))) {
            //Checks whether the user has added an image or not
            try{

                StorageReference filepath = mStorageRoot.child("Blog").child(resultUri.getLastPathSegment());
                progressDialog.show();
                filepath.putFile(resultUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Snackbar.make(relativeLayout, "Image upload failed", Snackbar.LENGTH_LONG)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startPosting();
                                    }
                                }).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressLint("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                        final DatabaseReference newPost = mDatabaseRoot.child("Blog").push();
                        final DatabaseReference postUserNotify = mDatabaseRoot.child("Clients").child(mAuth.getCurrentUser().getUid());
                        newPost.child("phone").setValue(phone_num);
                        newPost.child("desc").setValue(desc_val);
                        //Handles null pointer exception
                        try {
                            if (downloadUri != null) {
                                newPost.child("image").setValue(downloadUri.toString());
                                postUserNotify.child("Latest post").setValue(downloadUri.toString());
                            }
                            newPost.child("uid").setValue(mAuth.getCurrentUser().getUid());
                            DatabaseReference userData = mDatabaseRoot.child("Clients").child(mAuth.getCurrentUser().getUid());
                            final DatabaseReference imageKey = userData.child("Profile");
                            imageKey.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String pictureKey = dataSnapshot.getValue().toString();
                                    newPost.child("profile").setValue(pictureKey);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            final DatabaseReference usernameKey = userData.child("Username");
                            usernameKey.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userKey = dataSnapshot.getValue().toString();
                                    newPost.child("username").setValue(userKey);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } catch (NullPointerException pointer) {
                            pointer.printStackTrace();
                        }

                        newPost.child("email").setValue(mAuth.getCurrentUser().getEmail());

                        progressDialog.dismiss();
                        startActivity(new Intent(Post_To_Blog.this, Home.class));
                        finish();
                    }
                });
            }catch(NullPointerException ex){
                ex.printStackTrace();
                Toast.makeText(this, "Add an image first", Toast.LENGTH_SHORT).show();
            }

        } else {
            Snackbar.make(relativeLayout, "Input fields are empty", Snackbar.LENGTH_LONG).show();
        }*/
    }



///////////////////////////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setAspectRatio(4, 3)
                    .setAutoZoomEnabled(true)
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