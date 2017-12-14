package com.example.hp.firechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class GoodChatActivity extends AppCompatActivity {
    private String mChatUser;
    private Toolbar chat_Tool_bar;
    private DatabaseReference mRootRef;
    private String mChatUsername;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMsgView;
    private RecyclerView mMessagesList;
    // private SwipeRefreshLayout mRefreshLayout;
     private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    // private MessageAdapter mAdapter;
    private DatabaseReference mMessageDatabase;
    private static final int Total_items_to_load=10;
    private int mCurrentPage=1;
    private int itempos=0;
    private String mLastKey="";
    private String mPrevKey="";
    private DatabaseReference mFutureReference;
    private DatabaseReference mMessagesReference;
    private DatabaseReference mMessages;
    private DatabaseReference mDatabase;
    private String watching;
    private String image,imageUser;
    private int watch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_chat);
        mChatUser = getIntent().getStringExtra("Blog_id");
       // watching=getIntent().getStringExtra("watch");
      //  Toast.makeText(getApplicationContext(), watching, Toast.LENGTH_SHORT).show();
      //  watch=Integer.parseInt(watching);
      //  Toast.makeText(getApplicationContext(),mChatUser, Toast.LENGTH_SHORT).show();
        //  mChatUsername = getIntent().getStringExtra("user_name");
        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_Btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_Btn);
        mChatMsgView = (EditText) findViewById(R.id.chat_view_EditText);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        chat_Tool_bar = (Toolbar) findViewById(R.id.chat_tool_bar_bro);
        setSupportActionBar(chat_Tool_bar);
        //setting the adapter
        //from there to here
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("Messages").keepSynced(true);
        // getSupportActionBar().setTitle(mChatUsername);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list2);
        mLinearLayout = new LinearLayoutManager(GoodChatActivity.this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mTitleView = (TextView) findViewById(R.id.custom_display_name);
        mLastSeenView = (TextView) findViewById(R.id.custom_last_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);
        // mTitleView.setText(mChatUsername);
        //setting the adapter
        // mAdapter = new MessageAdapter(messagesList);


        // mMessagesList.setAdapter(mAdapter);
        //  mRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
        mFutureReference=FirebaseDatabase.getInstance().getReference().child("FutureMessages");
        mMessagesReference=FirebaseDatabase.getInstance().getReference().child("Messages");
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long k=dataSnapshot.getChildrenCount();
                mRootRef.child("seenMsgs").child(mCurrentUserId).child(mChatUser).child("seen").setValue(k).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                           // Toast.makeText(getApplicationContext(),"computed succesfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"computed unsuccesfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //loadMessages();
        //from there to here
        mDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageUser=dataSnapshot.child("image").getValue().toString();                      //
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("online")) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    image = dataSnapshot.child("image").getValue().toString();                  //
                    String name=dataSnapshot.child("name").getValue().toString();
                    mTitleView.setText(name);
                    if (online.equals("true")) {
                        mLastSeenView.setText("online");
                    } else {
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        Long lastTime = Long.parseLong(online);
                        String LastSeenTime = getTimeAgo.getTimeAgo(lastTime, GoodChatActivity.this);
                        mLastSeenView.setText(LastSeenTime);
                    }
                    if(!image.equals("default")) {
                        Picasso.with(GoodChatActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(GoodChatActivity.this).load(image).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mProfileImage);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {
                    mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue("false");

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }


        });
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent FutureMessaging = new Intent(GoodChatActivity.this, FutureMessagingActivity.class);
                //     Toast.makeText(getApplicationContext(),"going to loginactivity", Toast.LENGTH_SHORT).show();
                //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                FutureMessaging.putExtra("current_user",mCurrentUserId);
                FutureMessaging.putExtra("user_key",mChatUser);
                startActivity(FutureMessaging);
                //finish();
            }
        });


    }
    private void sendMessage() {
        try {
            final String message = mChatMsgView.getText().toString();
            if (!TextUtils.isEmpty(message)) {
                final DatabaseReference current_user_ref = mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser);
                final DatabaseReference chat_user_ref = mRootRef.child("Messages").child(mChatUser).child(mCurrentUserId);
                DatabaseReference user_msg_push = mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser).push();
                final String push_id_user = user_msg_push.getKey();
                current_user_ref.child(push_id_user).child("message").setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            current_user_ref.child(push_id_user).child("seen").setValue("false");
                            current_user_ref.child(push_id_user).child("type").setValue("text");
                            current_user_ref.child(push_id_user).child("from").setValue(mCurrentUserId);
                            current_user_ref.child(push_id_user).child("time").setValue(System.currentTimeMillis());
                            chat_user_ref.child(push_id_user).child("time").setValue(System.currentTimeMillis());
                            chat_user_ref.child(push_id_user).child("from").setValue(mCurrentUserId);
                            chat_user_ref.child(push_id_user).child("message").setValue(message);
                            chat_user_ref.child(push_id_user).child("seen").setValue("false");
                            chat_user_ref.child(push_id_user).child("type").setValue("text");
                            mChatMsgView.setText("");
                            current_user_ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long k=dataSnapshot.getChildrenCount();
                                    mMessagesList.scrollToPosition((int)k -1);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Toast.makeText(getApplicationContext(), "Message is sent successfully", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            } else {
                Toast.makeText(getApplicationContext(), "Please enter any message", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public   void onStart() {
        super.onStart();
        mMessages = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrentUserId).child(mChatUser);
        FirebaseRecyclerAdapter<GoodChat,GoodChatActivity.ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GoodChat, GoodChatActivity.ChatViewHolder>(
                GoodChat.class,
                R.layout.message_single_layout,
                GoodChatActivity.ChatViewHolder.class,
                mMessages
        ) {
            @Override
            protected void populateViewHolder(final GoodChatActivity.ChatViewHolder chatViewHolder, GoodChat chats, int position) {
               try {
                    final String user_key = getRef(position).getKey();int i=0;
                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String from_user = chats.getFrom();
                    // viewHolder.settings(from_user,current_user_id);
                    chatViewHolder.mMessageText.setText(chats.getMessage());
                  // ChatViewHolder.settings(time);


                    mDatabase.child(from_user).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            chatViewHolder.mMessageName.setText(dataSnapshot.child("name").getValue().toString());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                   mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser).child(user_key).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {
                           int i=0;
                           long time= Long.parseLong(dataSnapshot.child("time").getValue().toString());
                           String[] st=new String[3];
                           Date date = new Date(time);
                           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
                           sdf.setTimeZone(TimeZone.getTimeZone("GMT+05:30")); // give a timezone reference for formating (see comment at the bottom
                           String formattedDate = sdf.format(date);
                           StringTokenizer str=new StringTokenizer(formattedDate," ");
                           while(str.hasMoreElements()){
                               st[i++]=str.nextToken();
                           }
                           chatViewHolder.mTime.setText(st[1]);
                           i=0;
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });
                    if (current_user_id.equals(from_user)) {
                        chatViewHolder.mMessageText.setBackgroundColor(Color.WHITE);
                        //  Toast.makeText(getClass(),"uploading thumbnail2 is failed", Toast.LENGTH_SHORT).show();
                        Log.d("one", "current user id");

                        chatViewHolder.mMessageText.setTextColor(Color.BLACK);

                    } else {
                        chatViewHolder.mMessageText.setBackgroundResource(R.drawable.message_text_background);

                        chatViewHolder.mMessageText.setTextColor(Color.WHITE);

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        };
        mMessagesList.setAdapter(firebaseRecyclerAdapter);
        mMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long num = dataSnapshot.getChildrenCount();
                mMessagesList.scrollToPosition((int)num-1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        public  TextView mMessageText;
        public TextView mMessageName;
        public static CircleImageView profileImage;
        public  TextView mTime;
        View mView;
        public ChatViewHolder(View view){
            super(view);
            mView=view;
            mMessageText=(TextView)view.findViewById(R.id.message_text_layout);
            profileImage=(CircleImageView)view.findViewById(R.id.message_profile_layout);
            mMessageName=(TextView)view.findViewById(R.id.message_name_layout);
            mTime=(TextView)view.findViewById(R.id.message_time_layout);
        }

        public static void settings(long time) {
            long timer=time;int i=0;
            String[] st=new String[3];
            Date date = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+05:30")); // give a timezone reference for formating (see comment at the bottom
            String formattedDate = sdf.format(date);
            StringTokenizer str=new StringTokenizer(formattedDate," ");
                while(str.hasMoreElements()){
                    st[i++]=str.nextToken();
                }
              //  mTime.setText(st[1]);
            // System.out.println(formattedDate);
            // Log.d("time 5","ajay2 "+formattedDate);

        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.e("ajay123","wow1");
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null) {
            mDatabase.child(currentUser.getUid()).child("online").setValue("true");
            // mDatabase.child(CurrentUser.getUid()).child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ajay124","wow2");
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null) {
            mDatabase.child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
            // mDatabase.child(CurrentUser.getUid()).child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }


}
