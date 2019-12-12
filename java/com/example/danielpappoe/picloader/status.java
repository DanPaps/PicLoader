package com.example.danielpappoe.picloader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
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

public class status extends AppCompatActivity {

    private String user_uid;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRoot;
    private DatabaseReference mDatabaseRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            user_uid = mAuth.getCurrentUser().getUid();
        }

        final DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Clients").child(user_uid);

        final EditText edtStatus = (EditText) findViewById(R.id.input_status);
        Button btnStatus = (Button) findViewById(R.id.updateBtn);




        try {
            mUser.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String status = dataSnapshot.getValue().toString();
                        edtStatus.setText(status);

                    }else {
                        if(dataSnapshot != null){
                            String value = null;
                            Toast.makeText(status.this, "No status found", Toast.LENGTH_SHORT).show();
                            try {
                                value = dataSnapshot.getValue().toString();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            edtStatus.setText(value);
                            edtStatus.setSelectAllOnFocus(true);
                            edtStatus.setPadding(16,16,0,16);

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
        //Listeners for button
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(status.this);
                final RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_status);
                dialog.setMessage("Updating status");
                if (!TextUtils.isEmpty(edtStatus.getText().toString())){
                    dialog.show();
                    mUser.child("status").setValue(edtStatus.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialog.dismiss();
                            Toast.makeText(status.this, "Your status has been updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(status.this,MyProfile.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Snackbar.make(layout,"Status update failed",Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });



    }


}


