package com.example.danielpappoe.picloader.chats;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/*import com.bilson.apps.mime_chat.R;
import com.bilson.apps.mime_chat.models.GroupModel;
import com.bilson.apps.mime_chat.ui.activities.GroupChat;*/
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



/**
 * Created by Daniel Pappoe on 7/3/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.danielpappoe.picloader.R;

public class Tab2Groups extends Fragment{
/*
    View v;
    RecyclerView _rec;
    DatabaseReference _db, _members,_messages;
    FirebaseAuth auth;
    ProgressBar _bar;
    GridLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<GroupModel,GroupViewHolder> adapter;
*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_groups, container, false);
        return rootView;
    }
   /* @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v = view;

        //INIT ITEMS HERE
        _rec = (RecyclerView) v.findViewById(R.id._rec_groups);
        _rec.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(),2);
        _rec.setLayoutManager(layoutManager);
        _bar = (ProgressBar) v.findViewById(R.id._bar_groups);

        _db = FirebaseDatabase.getInstance().getReference().child(getString(R.string.app_name)).child(getString(R.string.groups));
        _members = FirebaseDatabase.getInstance().getReference().child(getString(R.string.app_name)).child(getString(R.string.members));
        _messages = FirebaseDatabase.getInstance().getReference().child(getString(R.string.app_name)).child(getString(R.string.group_messages));
        _members.keepSynced(true);
        _members.orderByKey();
        _db.keepSynced(true);
        auth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();

        _bar.setVisibility(View.VISIBLE);

        //FIREBASE RECYCLERVIEW
        adapter = new FirebaseRecyclerAdapter<GroupModel, GroupViewHolder>
                (
                        GroupModel.class,
                        R.layout.layout_group,
                        GroupViewHolder.class,
                        _db
                ) {
            @Override
            protected void populateViewHolder(GroupViewHolder groupViewHolder, GroupModel groupModel, int i) {
                _bar.setVisibility(View.INVISIBLE);

                groupViewHolder.setAdmin(groupModel.getAdmin());
                groupViewHolder.setIcon(groupModel.getIcon(),getContext());
                groupViewHolder.setName(groupModel.getName());
                groupViewHolder.setDate(groupModel.getDate());
                groupViewHolder.setGid(groupModel.getGid());

                //GET GROUP KEY
                final String _grp_key = getRef(i).getKey();
                final DatabaseReference _grp_ref = _db.child(_grp_key);
                _grp_ref.limitToFirst(10);
                _grp_ref.keepSynced(true);

                //GET CONTENT FORM GROUP CHAT
                groupViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _grp_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    try{
                                        String name = dataSnapshot.child("name").getValue().toString();
                                        String icon = dataSnapshot.child("icon").getValue().toString();
                                        String admin = dataSnapshot.child("admin").getValue().toString();
                                        String gid = dataSnapshot.child("gid").getValue().toString();
                                        String date = dataSnapshot.child("date").getValue().toString();

                                        Intent grpIntent = new Intent(getContext(), GroupChat.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("name",name);
                                        bundle.putString("icon",icon);
                                        bundle.putString("admin",admin);
                                        bundle.putString("gid",gid);
                                        bundle.putString("date",date);
                                        grpIntent.putExtras(bundle);
                                        startActivity(grpIntent);
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

                //REMOVE GROUP
                groupViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.delete_group);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _grp_ref.removeValue();
                                _members.child(_grp_key).getRef().removeValue();
                                try {
                                    _messages.child(_grp_key).getRef().removeValue();
                                    _messages.child(auth.getCurrentUser().getUid()).getRef().removeValue();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                Snackbar.make(v, R.string.remove_grp_confirmed,Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
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

    public static class GroupViewHolder extends RecyclerView.ViewHolder{

        View view;
        ImageView _grp_image;
        TextView _grp_name, _grp_admin,_grp_member,_grp_gid,_grp_date;

        public GroupViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;


            //VIEW
            _grp_admin = (TextView) view.findViewById(R.id._grp_admin);
            _grp_image = (ImageView) view.findViewById(R.id._grp_icon);
            _grp_name = (TextView) view.findViewById(R.id._grp_name);
            _grp_member = (TextView) view.findViewById(R.id._grp_member);
            _grp_gid = (TextView) view.findViewById(R.id._grp_gid);
            _grp_date = (TextView) view.findViewById(R.id._grp_date);
        }

        void setAdmin(String admin){
            _grp_admin = (TextView) view.findViewById(R.id._grp_admin);
            _grp_admin.setText(admin);
        }

        void setName(String name){
            _grp_name = (TextView) view.findViewById(R.id._grp_name);
            _grp_name.setText(name);
        }

        void setIcon(String icon, Context ctx){
            _grp_image = (ImageView) view.findViewById(R.id._grp_icon);
            if (!icon.equals("default")){
                Glide.with(ctx)
                        .load(icon)
                        .asBitmap()
                        .placeholder(R.drawable.nobody)
                        .error(R.drawable.notfound)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true)
                        .into(_grp_image);
            }
        }

        void setGid(String gid){
            _grp_gid = (TextView) view.findViewById(R.id._grp_gid);
            _grp_gid.setText(gid);
        }

        void setDate(String date){
            _grp_date = (TextView) view.findViewById(R.id._grp_date);
            _grp_date.setText(date);
        }
    }*/
}
