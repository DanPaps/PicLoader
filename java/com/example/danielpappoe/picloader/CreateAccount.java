package com.example.danielpappoe.picloader;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {
    TextInputEditText password;
    TextInputEditText email;
    TextInputEditText username;
    TextInputEditText phone;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseRoot;
    private ProgressDialog progressDialog;
    private TextInputEditText mEmail, mPassword;
    private RelativeLayout layout;
    private String mail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ////////////////////////////////////////////////////////////////////////////////////////////
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRoot = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Clients");
        mDatabaseRoot.keepSynced(true);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    progressDialog.dismiss();
                    startActivity(new Intent(CreateAccount.this, Home.class));
                    finish();
                }
            }
        };
        ////////////////////////////////////////////////////////////////////////////////////////////
        //Items
        progressDialog = new ProgressDialog(CreateAccount.this);
        progressDialog.setMessage("Connecting...");
        progressDialog.setCanceledOnTouchOutside(false);

        phone=(TextInputEditText)findViewById(R.id.phone);
        username=(TextInputEditText)findViewById(R.id.username);
        mEmail = (TextInputEditText) findViewById(R.id.email);
        mPassword = (TextInputEditText) findViewById(R.id.password);
        Button createAccount = (Button) findViewById(R.id.button1);
        ////////////////////////////////////////////////////////////////////////////////////////////
        //Listeners
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    Toast.makeText(getApplicationContext(), "Please enter Requirements", Toast.LENGTH_SHORT).show();

                }
                else {
                    CreateUser();
                }

            }
        });
    }

    private void CreateUser() {
        if (!TextUtils.isEmpty(mEmail.getText().toString()) && !TextUtils.isEmpty(mPassword.getText().toString())){
            mail = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(mail, password).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateAccount.this, "Registration unsuccessful", Toast.LENGTH_SHORT).show();

                    //Snackbar.make(layout,"Registration unsuccessful",Snackbar.LENGTH_LONG).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String user_uid = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabaseRoot.child(user_uid);
                        current_user_db.child("email").setValue(mail);
                        current_user_db.child("username").setValue("username");
                        current_user_db.child("profile").setValue("default");
                        current_user_db.child("status").setValue("Hey there am using PicsUpload");

                    }

                }
            });
        }else {
            Toast.makeText(CreateAccount.this, "Enter your email and password", Toast.LENGTH_SHORT).show();
            //Snackbar.make(layout,"Enter your email and password", Snackbar.LENGTH_LONG).show();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////// /////
    //////////End of listeners

    // Handling empty fields method
    private boolean validate() {

        boolean valid =true;
        if((username.getText().toString()).isEmpty()){
            username.setError("Enter Username");
            valid=false;
        }
        if ((mEmail.getText().toString()).isEmpty()) {
            mEmail.setError("Enter your email");
            valid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()) {
            mEmail.setError("Please enter a valid email");
            valid = false;
        }
        if((phone.getText().toString().length())!=10){
            phone.setError("Phone number should be 10 digits");
        }
        if((phone.getText().toString()).isEmpty()){
            phone.setError("Enter phone number");
        }
        int a=7;
        if((mPassword.getText().toString().length())<a){
            mPassword.setError("Password should be at least 7 characters");
            valid = false;
        }

        if ((mPassword.getText().toString()).isEmpty()) {
            mPassword.setError("Password cannot be left empty");
            valid = false;
        }
        return valid;
    }
    // End ofHandling empty fields method

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



}

