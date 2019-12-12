package com.example.danielpappoe.picloader;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//import io.smooch.core.App;


public class login extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;

    public static String LoggedIn_User_Email;
    public static int Device_Width;

    Button loginButton;
    TextInputEditText password;
    TextInputEditText email;
    TextView createAccount;


    ////On create ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /////////////////////////////////////////////////////////////
        password =(TextInputEditText) findViewById(R.id.password);
        email=(TextInputEditText)findViewById(R.id.email);
        createAccount=(TextView)findViewById(R.id.createAccount);
        //////////////////////////////////////////////////////////////
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in");
        progressDialog.setCanceledOnTouchOutside(false);


        //FireBase Init
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    LoggedIn_User_Email=user.getEmail();
                    // User is signed in
                    progressDialog.dismiss();
                    startActivity(new Intent(login.this,Home.class));
                    finish();
                    //      Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
            }
        };

        DisplayMetrics metrics =getApplicationContext().getResources().getDisplayMetrics();
        Device_Width =metrics.widthPixels;

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        ////// To login exiting users when the login button is clicked
        loginButton =(Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!validate()) {
                            Toast.makeText(getApplicationContext(), "Please enter Requirements", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            SignInUser();
                        }


                    }
                }
        );

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////// When the create account button is clicked
        createAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(login.this, CreateAccount.class);
                        startActivity(i);
                    }
                }
        );

 /*       FirebaseUser user = mAuth.getCurrentUser();
        if (user !=null){
            LoggedIn_User_Email=user.getEmail();
        }*/


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////out side the on create method////////////////////////////////////////////////////////////////////////////

    // Handling empty fields method
    private boolean validate() {
        boolean valid = true;
        int a=7;
        if((password.getText().toString().length())<a){
            password.setError("Password should be at least 7 characters");
            valid = false;
        }
        if ((email.getText().toString()).isEmpty()) {
            email.setError("Enter your email");
            valid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("Please enter a valid email");
            valid = false;
        }

        if ((password.getText().toString()).isEmpty()) {
            password.setError("Password cannot be left empty");
            valid = false;
        }
        return valid;
    }

    private void SignInUser() {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(login.this,"sorry",Toast.LENGTH_LONG).show();

            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(login.this,Home.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }


}

