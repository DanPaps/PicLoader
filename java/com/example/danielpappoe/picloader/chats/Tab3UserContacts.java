package com.example.danielpappoe.picloader.chats;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*import com.bilson.apps.mime_chat.R;
import com.bilson.apps.mime_chat.models.ContactModel;*/
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.danielpappoe.picloader.CreateAccount;
import com.example.danielpappoe.picloader.login;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
//

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.danielpappoe.picloader.R;

/**
 * Created by Daniel Pappoe on 7/3/2017.
 */


public class Tab3UserContacts extends Fragment {
    View v;
    private RecyclerView _rec;
    private DatabaseReference _db;
    private FirebaseAuth auth;
    private ProgressBar _bar;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_user_contacts, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v = view;

        //INIT ITEMS HERE
        _rec = (RecyclerView) v.findViewById(R.id._rec_contacts);
        _rec.setHasFixedSize(true);
        _rec.setLayoutManager(new LinearLayoutManager(getContext()));
        _bar = (ProgressBar) v.findViewById(R.id._bar_contacts);

        _db = FirebaseDatabase.getInstance().getReference().child("Kreative");
        _db.keepSynced(true);
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
       // auth.addAuthStateListener(mAuthListener);

        _bar.setVisibility(View.VISIBLE);
        //FIREBASE RECYCLERVIEW
        FirebaseRecyclerAdapter<ContactModel,ContactViewHolder> adapter = new FirebaseRecyclerAdapter<ContactModel, ContactViewHolder>
                (
                        ContactModel.class,
                        R.layout.layout_contacts,
                        ContactViewHolder.class,
                        _db.child("Clients")
                ) {
            @Override
            protected void populateViewHolder(final ContactViewHolder contactViewHolder, ContactModel contactModel, final int i) {
                _bar.setVisibility(View.INVISIBLE);

                if(contactModel.getEmail().equals(login.LoggedIn_User_Email)){
                    contactViewHolder.Layout_hide();        //to prevent you from chatting with your self
                }else {
                    contactViewHolder.setUsername(contactModel.getUsername());
                    contactViewHolder.setStatus(contactModel.getStatus());
                    contactViewHolder.setProfile(contactModel.getProfile(), getContext());
                }
                /*contactViewHolder.setUsername(contactModel.getUsername());
                contactViewHolder.setStatus(contactModel.getStatus());
                contactViewHolder.setProfile(contactModel.getProfile(), getContext());*/

                //GET REF KEYS
                final String _contactUid = getRef(i).getKey();
                final DatabaseReference _user = _db.child(_contactUid);
                _user.limitToFirst(100);        //Display the first 100 contacts
                _user.keepSynced(true);

                final String key =getRef(i).getKey();

                //get item postion
                contactViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getClickFeedBack(contactViewHolder,key);
                    }
                });

                /*
                //LISTENERS
                //RECYCLERVIEW
                contactViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        _user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    try{
                                        final String _name = dataSnapshot.child("username").getValue().toString();
                                       // final String _uid = dataSnapshot.child("uid").getValue().toString();
                                     //   final String _seen = dataSnapshot.child("seen").getValue().toString();
                                      //  final String _date = dataSnapshot.child("date").getValue().toString();
                                        final String _profile = dataSnapshot.child("profile").getValue().toString();
                                        final String _email = dataSnapshot.child("email").getValue().toString();
                                       // final String _number = dataSnapshot.child("number").getValue().toString();
                                        final String _status = dataSnapshot.child("status").getValue().toString();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setMessage(String.format("Do you want to add %s to your chats?",_name));
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //ADD CHAT TO CHAT DATABASE
                                                DatabaseReference _chats = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Chats")
                                                        .child(auth.getCurrentUser().getUid()).child(_contactUid);
                                                _chats.child("username").setValue(_name);
                                                _chats.child("email").setValue(_email);
                                                //_chats.child("uid").setValue(_uid);
                                                _chats.child("profile").setValue(_profile);
                                              //  _chats.child("date").setValue(_date);
                                              //  _chats.child("seen").setValue(_seen);
                                               // _chats.child("number").setValue(_number);
                                                _chats.child("status").setValue(_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isComplete())
                                                            Toast.makeText(getContext(), "Chat added successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Cannot add new chat", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        });

                                        builder.show();
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        return true;
                    }
                });
*//**/
/*
                //IMAGE BUTTON
                contactViewHolder._button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        _user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    try{
                                        final String _name = dataSnapshot.child("username").getValue().toString();
                                      //  final String _uid = dataSnapshot.child("uid").getValue().toString();
                                      //  final String _seen = dataSnapshot.child("seen").getValue().toString();
                                      //  final String _date = dataSnapshot.child("date").getValue().toString();
                                        final String _profile = dataSnapshot.child("profile").getValue().toString();
                                        final String _email = dataSnapshot.child("email").getValue().toString();
                                      //  final String _number = dataSnapshot.child("number").getValue().toString();
                                        final String _status = dataSnapshot.child("status").getValue().toString();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setMessage(String.format("Do you want to add %s to your chats?",_name));
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //ADD CHAT TO CHAT DATABASE
                                                DatabaseReference _chats = FirebaseDatabase.getInstance().getReference().child("Mime-Chat").child("Chats")
                                                        .child(auth.getCurrentUser().getUid()).child(_contactUid);
                                                _chats.child("name").setValue(_name);
                                                _chats.child("email").setValue(_email);
                                               // _chats.child("uid").setValue(_uid);
                                                _chats.child("profile").setValue(_profile);
                                              //  _chats.child("date").setValue(_date);
                                               // _chats.child("seen").setValue(_seen);
                                               // _chats.child("number").setValue(_number);
                                                _chats.child("status").setValue(_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isComplete())
                                                            Toast.makeText(getContext(), "Chat added successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Snackbar.make(v,"Cannot add new chat",Snackbar.LENGTH_LONG).show();
                                                    }
                                                });

                                            }
                                        });

                                        builder.show();
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });


                //LISTENERS
                contactViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    try{
                                        final String _name = dataSnapshot.child("name").getValue().toString();
                                        final String _uid = dataSnapshot.child("uid").getValue().toString();
                                        final String _seen = dataSnapshot.child("seen").getValue().toString();
                                        final String _date = dataSnapshot.child("date").getValue().toString();
                                        final String _profile = dataSnapshot.child("profile").getValue().toString();
                                        final String _email = dataSnapshot.child("email").getValue().toString();
                                        final String _number = dataSnapshot.child("number").getValue().toString();
                                        final String _status = dataSnapshot.child("status").getValue().toString();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setMessage(String.format("Do you want to add %s to your chats?",_name));
                                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //ADD CHAT TO CHAT DATABASE
                                                DatabaseReference _chats = FirebaseDatabase.getInstance().getReference().child("Mime-Chat").child("Chats")
                                                        .child(auth.getCurrentUser().getUid()).child(_contactUid);
                                                _chats.child("name").setValue(_name);
                                                _chats.child("email").setValue(_email);
                                                _chats.child("uid").setValue(_uid);
                                                _chats.child("profile").setValue(_profile);
                                                _chats.child("date").setValue(_date);
                                                _chats.child("seen").setValue(_seen);
                                                _chats.child("number").setValue(_number);
                                                _chats.child("status").setValue(_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isComplete())
                                                            Toast.makeText(getContext(), "Chat added successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Cannot add new chat", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        });

                                        builder.show();
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
*/


            }
        };

        _rec.setAdapter(adapter);

    }

    private  void getClickFeedBack(ContactViewHolder viewHolder, final String key){
        final Intent intent =new Intent(viewHolder.view.getContext(),ChatInterface.class);
        final Bundle bundle=new Bundle();

        DatabaseReference chatRef = _db.child("Clients").child(key);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("username").getValue().toString();
                    String profile = dataSnapshot.child("profile").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();


                    bundle.putString(ChatInterface.EXTRA_USER,getString(R.string.app_name)); //dummy
                    bundle.putString(ChatInterface.DATA_NAME,name);
                    bundle.putString(ChatInterface.DATA__PROFILE,profile);
                    bundle.putString(ChatInterface.DATA_EMAIL,email);
                    bundle.putString(ChatInterface.DATA_STATUS_,status);
                    bundle.putString(ChatInterface.DATA_KEY,key);

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{
         View view;
        public TextView _name, _status;
        public CircleImageView _image;
        public ImageButton _button;
        public LinearLayout layout;
        final LinearLayout.LayoutParams params;

        public ContactViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            //INIT ITEMS HERE
            _image = (CircleImageView) view.findViewById(R.id._profile_contact);
            _name = (TextView) view.findViewById(R.id._name_contact);
            _status = (TextView) view.findViewById(R.id._status_contact);
            _button = (ImageButton) view.findViewById(R.id._contact_add);
            layout =(LinearLayout) view.findViewById(R.id.layout_contacts);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void setUsername(String name){
          final TextView  _name1 = (TextView) view.findViewById(R.id._name_contact);
            _name1.setText(name);
        }

        public void setStatus(String status){
            final TextView _status1 = (TextView) view.findViewById(R.id._status_contact);
            _status1.setText(status);
        }
        private  void Layout_hide(){
            params.height =0;
            layout.setLayoutParams(params);
        }

        public void setProfile(String profile, Context _ctx){
            final CircleImageView _image1 = (CircleImageView) view.findViewById(R.id._profile_contact);
            Glide.with(_ctx).load(profile).asBitmap().placeholder(R.drawable.nobody).into(_image1);
           /* if (!profile.equals("default")){
                Glide.with(_ctx)
                        .load(profile)
                        .asBitmap()
                        .placeholder(R.drawable.nobody)
                        .error(R.drawable.notfound)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .dontAnimate()
                        .into(_image);
            }*/
        }
    }

    @Override
    public void onDestroyView() {

        //USER LAST SEEN IS SET
        super.onDestroyView();
        if (auth.getCurrentUser() != null){
            //_db.child(auth.getCurrentUser().getUid()).child("seen").setValue(DateFormat.getTimeInstance().format(new Date()));
        }

    }
}
