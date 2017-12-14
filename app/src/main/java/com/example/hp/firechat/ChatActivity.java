package com.example.hp.firechat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
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
    private SwipeRefreshLayout mRefreshLayout;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private DatabaseReference mMessageDatabase;
    private static final int Total_items_to_load=10;
    private int mCurrentPage=1;
    private int itempos=0;
    private String mLastKey="";
    private String mPrevKey="";
    private DatabaseReference mFutureReference;
    private DatabaseReference mMessagesReference;
    private DatabaseReference mDatabase;
   // private Thread t;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);
            mChatUser = getIntent().getStringExtra("Blog_id");
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
            mTitleView = (TextView) findViewById(R.id.custom_display_name);
            mLastSeenView = (TextView) findViewById(R.id.custom_last_seen);
            mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);
           // mTitleView.setText(mChatUsername);
            //setting the adapter
            mAdapter = new MessageAdapter(messagesList);
            mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
            mLinearLayout = new LinearLayoutManager(this);
            mMessagesList.setHasFixedSize(true);
            mMessagesList.setLayoutManager(mLinearLayout);
            mMessagesList.setAdapter(mAdapter);
            mRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);
            mFutureReference=FirebaseDatabase.getInstance().getReference().child("FutureMessages");
            mMessagesReference=FirebaseDatabase.getInstance().getReference().child("Messages");
            mDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
            loadMessages();
            //from there to here
            mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("online")) {
                        String online = dataSnapshot.child("online").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        mTitleView.setText(name);
                        if (online.equals("true")) {
                            mLastSeenView.setText("online");
                        } else {
                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            Long lastTime = Long.parseLong(online);
                            String LastSeenTime = getTimeAgo.getTimeAgo(lastTime, ChatActivity.this);
                            mLastSeenView.setText(LastSeenTime);
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
                            public void onComplete(@NonNull Task<Void> task) {
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
            // message sending buttton onclick listener
            mChatSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });
            mChatAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent FutureMessaging = new Intent(ChatActivity.this, FutureMessagingActivity.class);
                    //     Toast.makeText(getApplicationContext(),"going to loginactivity", Toast.LENGTH_SHORT).show();
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    FutureMessaging.putExtra("current_user",mCurrentUserId);
                    FutureMessaging.putExtra("user_key",mChatUser);
                    startActivity(FutureMessaging);
                    //finish();
                }
            });
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mCurrentPage++;
                   // messagesList.clear();
                    itempos=0;
                    loadMoreMessages();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        } /*
         t=new Thread(){
            public String ftimestamp;
            public String reqMessage;
            Long timebro;
            public void run(){
                Log.e("thread11","thread is executing");
                mFutureReference.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(mChatUser)){
                            ftimestamp=dataSnapshot.child(mChatUser).child("ftimestamp").getValue().toString();
                            reqMessage=dataSnapshot.child(mChatUser).child("message").getValue().toString();
                            timebro=Long.parseLong(ftimestamp);
                            startCount();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
                void startCount(){
                    if (flag == 1) {
                        return;
                    }
                    if (timebro > System.currentTimeMillis()) {
                        try {
                            currentThread().wait(timebro - System.currentTimeMillis());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e("excep","exception vachindi roy");
                        }
                    } else {
                        final DatabaseReference current_user_ref = mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser);
                        final DatabaseReference chat_user_ref = mRootRef.child("Messages").child(mChatUser).child(mCurrentUserId);
                        DatabaseReference user_msg_push = mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser).push();
                        final String push_id_user = user_msg_push.getKey();
                        current_user_ref.child(push_id_user).child("message").setValue(reqMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    current_user_ref.child(push_id_user).child("seen").setValue("false");
                                    current_user_ref.child(push_id_user).child("type").setValue("text");
                                    current_user_ref.child(push_id_user).child("from").setValue(mCurrentUserId);
                                    current_user_ref.child(push_id_user).child("time").setValue(ServerValue.TIMESTAMP);
                                    chat_user_ref.child(push_id_user).child("time").setValue(ServerValue.TIMESTAMP);
                                    chat_user_ref.child(push_id_user).child("from").setValue(mCurrentUserId);
                                    chat_user_ref.child(push_id_user).child("message").setValue(reqMessage);
                                    chat_user_ref.child(push_id_user).child("seen").setValue("false");
                                    chat_user_ref.child(push_id_user).child("type").setValue("text");
                                    mFutureReference.child(mCurrentUserId).child(mChatUser).removeValue();
                                    mFutureReference.child(mChatUser).child(mCurrentUserId).removeValue();

                                    Toast.makeText(getApplicationContext(), "futureMessage is sent successfully", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                    if (flag == 1) {
                        return;
                    }
                    Log.e("thread12", "thread is completed");
                     }

        }; */
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef=mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Messages message = dataSnapshot.getValue(Messages.class);
                    String messageKey=dataSnapshot.getKey();
                    if (!mPrevKey.equals(messageKey)) {

                        messagesList.add(itempos++,message);
                    }else{
                        mPrevKey= mLastKey;
                    }
                    if(itempos==1){
                        mLastKey=messageKey;
                    }
                    mAdapter.notifyDataSetChanged();
                    mMessagesList.scrollToPosition(messagesList.size()-1);
                    mRefreshLayout.setRefreshing(false);
                    mLinearLayout.scrollToPositionWithOffset(10,0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMessages() {
        DatabaseReference messageRef=mRootRef.child("Messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage*Total_items_to_load);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Messages message = dataSnapshot.getValue(Messages.class);
                    itempos++;
                    if(itempos==1){
                        String messageKey=dataSnapshot.getKey();
                        mLastKey=messageKey;
                        mPrevKey=messageKey;
                    }
                    messagesList.add(message);
                    mAdapter.notifyDataSetChanged();
                    mMessagesList.scrollToPosition(messagesList.size()-1);
                    mRefreshLayout.setRefreshing(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                            current_user_ref.child(push_id_user).child("time").setValue(ServerValue.TIMESTAMP);
                            chat_user_ref.child(push_id_user).child("time").setValue(ServerValue.TIMESTAMP);
                            chat_user_ref.child(push_id_user).child("from").setValue(mCurrentUserId);
                            chat_user_ref.child(push_id_user).child("message").setValue(message);
                            chat_user_ref.child(push_id_user).child("seen").setValue("false");
                            chat_user_ref.child(push_id_user).child("type").setValue("text");
                            mChatMsgView.setText("");

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
    protected void onStart() {
        super.onStart();
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
        flag=1;
        Log.e("ajay124","wow2");
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null) {
            mDatabase.child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
            // mDatabase.child(CurrentUser.getUid()).child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }
}
