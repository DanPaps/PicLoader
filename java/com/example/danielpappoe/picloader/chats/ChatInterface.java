package com.example.danielpappoe.picloader.chats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import java.text.DateFormat;
import java.util.Date;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.danielpappoe.picloader.R;
import com.example.danielpappoe.picloader.login;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatInterface extends AppCompatActivity {
    public static final String EXTRA_USER="EXTRA_USER";
    //intents strings

    public static final String DATA_KEY="DATA_KEY";
    public static final String DATA_NAME="DATA_NAME";
    public static final String DATA__PROFILE="DATA__PROFILE";
    public static final String DATA_EMAIL="DATA_EMAIL";
    public static final String DATA_STATUS_="DATA_STATUS_";

    //firebase
    String sender="";
    String receipient="";

    //toolbar variabls
    private String status;
    private String name;
    private String profile;
    private static String email;
    private String key;
    private static String email1;



    FirebaseAuth auth;
    DatabaseReference db;
    DatabaseReference dbSender;
    DatabaseReference dbRec;



    RecyclerView messagesLists;
    ImageButton addpic;
    EditText enterMessage;
    ImageView send;
    RecyclerView messagelist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_interface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        auth =FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Kreative").child("Messages");
        db.keepSynced(true);
                // TODO: 8/6/2017


        messagesLists =(RecyclerView)findViewById(R.id._rec_chat_interface);
        addpic =(ImageButton)findViewById(R.id.single_chat_activity_emoji);
        enterMessage =(EditText)findViewById(R.id.user_message);
        send =(ImageView)findViewById(R.id._fabSend);

        messagesLists =(RecyclerView)findViewById(R.id._rec_chat_interface);
        messagesLists.setHasFixedSize(true);
        LinearLayoutManager manager =new LinearLayoutManager(ChatInterface.this);
        manager.setStackFromEnd(true);
        messagesLists.setLayoutManager(manager);


        Intent intent =getIntent();
        if (intent.hasExtra(EXTRA_USER)){
            status = intent.getExtras().getString(DATA_STATUS_);
            name = intent.getExtras().getString(DATA_NAME);
            profile = intent.getExtras().getString(DATA_STATUS_);
            email = intent.getExtras().getString(DATA_EMAIL);
            key = intent.getExtras().getString(DATA_KEY);
            email1=email;

            //setprofile with glide


            ContactModel newUser =new ContactModel(name,status,profile,email);
            bindUser(newUser);
////*/            ActionBar actionBar =getSupportActionBar();
//            if(toolbar !=null){
//                toolbar.setTitle(Html.fromHtml("<font color=#FFFFFF" + getIntent().getStringExtra(name) +"</font>"));
//
//            }
//*/
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        enterMessage.addTextChangedListener(textWatcher);
        addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 8/6/2017
            }
        });

    }



    private void bindUser(ContactModel newUser) {
        if(newUser==null)
            return;
        if(auth.getCurrentUser() !=null) {
            sender = auth.getCurrentUser().getEmail().replace("@", "").replace(".", "");

            receipient =newUser.getEmail().replace("@", "").replace(".", "");

            dbSender=  db.child(auth.getCurrentUser().getUid()).child(sender).child(receipient);
            dbSender.keepSynced(true);
            dbRec=  db.child(key).child(receipient).child(sender);
            dbRec.keepSynced(true);

        }

    }
    TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            send.setImageDrawable(getDrawable(R.drawable.ic_keyboard_voice_black_24dp));
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                send.setImageTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            send.setImageDrawable(getDrawable(R.drawable.ic_send_black_24dp));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                send.setImageTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));
            }
        }
    };

    private void sendMessage() {
        if (sender == null|| receipient ==null)
            return;
        String s = enterMessage.getText().toString();
        if (TextUtils.isEmpty(s))return;
        DatabaseReference push =dbSender.push();
        DatabaseReference push1=dbRec.push();

        final ArrayMap<String, String> map = new ArrayMap<>();
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        map.put("body", s);
        map.put("id", key);
        map.put("sender", name);
        map.put("time",dateFormat.format(new Date(System.currentTimeMillis())));

        push.setValue(map); //senders db
        push1.setValue(map); //recipient db

        enterMessage.setText("");
        enterMessage.requestFocus();

        messagesLists.postDelayed(new Runnable() {
            @Override
            public void run() {
                messagesLists.smoothScrollToPosition(messagesLists.getAdapter().getItemCount()-1);
            }
        }, 500);


    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Messages,MessagesViewHolder> adapter = new FirebaseRecyclerAdapter<Messages, MessagesViewHolder>(
                Messages.class,
                R.layout.message_row,
                MessagesViewHolder.class,
                dbSender
        ) {
            @Override
            protected void populateViewHolder(MessagesViewHolder viewHolder, Messages model, final int position) {
                viewHolder.messageItem.setText(model.getBody().trim());
                viewHolder.messageTime.setText(model.getTime().trim());
                final String key =getRef(position).getKey();

                viewHolder.v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showAlertDialog(key,position);
                        return true;
                    }
                });
            }
        };

      //  adapter.setHasStableIds(true);
        messagesLists.setAdapter(adapter);
//        messagesLists.getAdapter().setHasStableIds(true);
        if (adapter.getItemCount() >0) {
            messagesLists.postDelayed(new Runnable() {
                @Override
                public void run() {
                    messagesLists.smoothScrollToPosition(adapter.getItemCount()-1); //scrol to last message
                    adapter.notifyItemInserted(adapter.getItemCount()-1);  //notyfy that neww ;
                }
            },500);
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void showAlertDialog(final String key, int position) {
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String[] options ={"Delete message","forward","Reply","Hide"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        deleteMessage(key);
                        break;
                    case 2:
                        forwordMessages(key);
                        break;
                    case 3:
                       reply(key);
                        break;
                    case  4:
                        hideMessage(key);
                        break;
                                    }
            }
        }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();

    }

    private void reply(String key) {

    }

    private void hideMessage(String key) {

    }

    private void forwordMessages(String key) {

    }

    private void deleteMessage(String key) {

    }


    //message ViewHolder
    public  static class  MessagesViewHolder extends RecyclerView.ViewHolder{
    View v;
        TextView messageItem;
        TextView messageTime;

      /*  final LinearLayout.LayoutParams params,text_params;
        LinearLayout layout;*/


    public MessagesViewHolder(View itemView) {
        super(itemView);
        this.v =itemView;
        messageItem=(TextView)v.findViewById(R.id.message_item);
        messageTime=(TextView)v.findViewById(R.id.message_time);

        /*params =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        text_params =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layout =(LinearLayout)v.findViewById(R.id.chat_linear_layout);*/

        alignMessages();
    }

/*        private void getSender(String email){
            email = email1;
            if (email.equals(login.LoggedIn_User_Email)){
                params.setMargins((login.Device_Width/3),5,10,10);
                text_params.setMargins(15,10,0,5);
                messageItem.setLayoutParams(params);
                v.setLayoutParams(params);
                v.setBackgroundResource(R.drawable.shape_outcoming_messages);
            }
            else
            {
                params.setMargins(10,0,(login.Device_Width/3),10);
                messageItem.setGravity(Gravity.START);
                text_params.setMargins(60,10,0,5);
                messageItem.setLayoutParams(text_params);
                v.setLayoutParams(params);
                v.setBackgroundResource(R.drawable.shape_incoming_message);
            }
        }*/

        private void alignMessages() {
            // TODO: 8/6/2017  
        }
    }

}
