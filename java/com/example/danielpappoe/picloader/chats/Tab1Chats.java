package com.example.danielpappoe.picloader.chats;

/**
 * Created by Daniel Pappoe on 7/3/2017.
 */

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*import com.bilson.apps.mime_chat.R;
import com.bilson.apps.mime_chat.models.ChatModel;
import com.bilson.apps.mime_chat.ui.activities.ChatInterface;*/
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import de.hdodenhof.circleimageview.CircleImageView;

/*import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;*/

import com.example.danielpappoe.picloader.R;


public class Tab1Chats extends Fragment{
   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_chats, container, false);
        return rootView;
    }*/

    View v;
    RecyclerView _rec;
    DatabaseReference _db;
    FirebaseAuth auth;
    ProgressBar _bar;
    public static String imageUrl = null;
    //String nr = "https://firebasestorage.googleapis.com/v0/b/projects-2017.appspot.com/o/Mime-Chat%2FUsers%2FAUR7K7BUj0MqhAlj35moVpw9SqJ2%2Fcropped271900751775631673.jpg?alt=media&token=b22684b0-8cca-4058-950a-d2040458af65";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab1_chats,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v = view;

        //INIT ITEMS HERE
        _rec = (RecyclerView) v.findViewById(R.id._rec_chats);
        _rec.setHasFixedSize(true);
        _rec.setLayoutManager(new LinearLayoutManager(getContext()));
        _bar = (ProgressBar) v.findViewById(R.id._bar_chats);
        auth = FirebaseAuth.getInstance();
        _db = FirebaseDatabase.getInstance().getReference().child("Mime-Chat").child("Chats").child(auth.getCurrentUser().getUid());
        _db.keepSynced(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        _bar.setVisibility(View.VISIBLE);
        //FIREBASE RECYCLERVIEW
        FirebaseRecyclerAdapter<ChatModel,ChatViewHolder> adapter = new FirebaseRecyclerAdapter<ChatModel, ChatViewHolder>
                (
                        ChatModel.class,
                        R.layout.layout_chat,
                        ChatViewHolder.class,
                        _db
                ) {
            @Override
            protected void populateViewHolder(final ChatViewHolder chatViewHolder, ChatModel chatModel, int i) {
                _bar.setVisibility(View.GONE);

                chatViewHolder.setUsername(chatModel.getUsername());
                chatViewHolder.setProfile(chatModel.getProfile(),getContext());
                chatViewHolder.setStatus(chatModel.getStatus());
                chatViewHolder.setTime(chatModel.getSeen());

                //LISTENERS
                //DISPLAY IMAGE IN ALERT DIALOG BOX
                chatViewHolder._image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        ImageView imageView = new ImageView(getContext());
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setAdjustViewBounds(true);
                        imageView.setImageDrawable(chatViewHolder._image.getDrawable());
                        builder.setView(imageView);

                        builder.show();
                    }
                });

                //GET THE POST KEY
                String _postKey = getRef(i).getKey();
                final DatabaseReference _receiver = _db.child(_postKey);
                _receiver.keepSynced(true);
                _receiver.limitToFirst(30);     //display the first 30 chats

                if (_postKey.equals(auth.getCurrentUser().getUid())){
                    chatViewHolder._name.setText(R.string.me);
                }

                //SEND USER TO CHAT SCREEN
                chatViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        _receiver.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    try {
                                        String _name = dataSnapshot.child("name").getValue().toString();
                                        String _uid = dataSnapshot.child("uid").getValue().toString();
                                        String _seen = dataSnapshot.child("seen").getValue().toString();
                                        String _date = dataSnapshot.child("date").getValue().toString();
                                        String _profile = dataSnapshot.child("profile").getValue().toString();
                                        String _email = dataSnapshot.child("email").getValue().toString();
                                        String _number = dataSnapshot.child("number").getValue().toString();
                                        String _status = dataSnapshot.child("status").getValue().toString();

                                        //SEND THE TEXTS TO THE NEXT INTENT
                                        Intent chat = new Intent(getContext(), ChatInterface.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("name",_name);
                                        bundle.putString("uid",_uid);
                                        bundle.putString("seen",_seen);
                                        bundle.putString("date",_date);
                                        bundle.putString("profile",_profile);
                                        bundle.putString("email",_email);
                                        bundle.putString("number",_number);
                                        bundle.putString("status",_status);

                                        chat.putExtras(bundle);
                                        startActivity(chat);

                                    } catch (NullPointerException e) {
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

                //REMOVE THE CURRENT CHAT
                chatViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setMessage("Delete chat with this user?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(getView(),"Removing chat from database",Snackbar.LENGTH_LONG).show();
                                _receiver.removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        databaseReference.removeValue();
                                        DatabaseReference _msgRef = FirebaseDatabase.getInstance().getReference().child("Mime-Chat")
                                                .child("Messages").child(auth.getCurrentUser().getEmail().replace("@","")
                                                        .replace(".",""));
                                        _msgRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(getContext(), "Chat was successfully removed", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(getContext(), "Chat was not removed", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    }
                                });
                            }
                        }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

                        return true;
                    }
                });


            }
        };

        _rec.setAdapter(adapter);

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView _name, _status, _time;
        CircleImageView _image;


        public ChatViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            //INIT ITEMS HERE
            _image = (CircleImageView) view.findViewById(R.id._profile_chat);
            _name = (TextView) view.findViewById(R.id._name_chat);
            _time = (TextView) view.findViewById(R.id._time_chat);
            _status = (TextView) view.findViewById(R.id._status_chat);
        }

        public void setUsername(String name){
            _name = (TextView) view.findViewById(R.id._name_chat);
            _name.setText(name);
        }

        public void setTime(String time){
            _time = (TextView) view.findViewById(R.id._time_chat);
            _time.setText(time);
        }

        public void setStatus(String status){
            _status = (TextView) view.findViewById(R.id._status_chat);
            _status.setText(status);
        }


        public void setProfile(String profile, Context _ctx){
            _image = (CircleImageView) view.findViewById(R.id._profile_chat);

            imageUrl = profile;
            if (!profile.equals("default")){
                Glide.with(_ctx)
                        .load(profile)
                        .asBitmap()
                        .placeholder(R.drawable.nobody)
                        .error(R.drawable.notfound)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .skipMemoryCache(true)
                        .centerCrop()
                        .dontAnimate()
                        .into(_image);
            }
        }
    }
}
