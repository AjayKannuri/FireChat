package com.example.hp.firechat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    String user_key=null;
    private DatabaseReference mDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private ImageView Profile_image;
    private Button mSendRequestBtn;
    private TextView mProfileFriendsCount;
    private Button mProfileCancelRequestBtn;
    private String mCurrent_state=null;
    private Toolbar Profile_tool;
    private ProgressDialog PD=null;
    private FirebaseAuth mAuth=null;
    private String current_user;
    String Name=null;
    private DatabaseReference mRootRef;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //mDisplayImage=(CircleImageView)findViewById(R.id.profile_single_image);
        mName=(TextView)findViewById(R.id.profile_name);
        mAuth=FirebaseAuth.getInstance();
        mStatus=(TextView)findViewById(R.id.profile_status);
        current_user=mAuth.getCurrentUser().getUid();
        Profile_image=(ImageView)findViewById(R.id.profile_single_image);
        mSendRequestBtn=(Button)findViewById(R.id.Profile_Send_requestBtn);
        mProfileFriendsCount=(TextView)findViewById(R.id.Profile_totalFriends);
        mProfileCancelRequestBtn=(Button)findViewById(R.id.Profile_cancel_requestBtn);
        mFriendReqDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("Notifications");
        PD = new ProgressDialog(ProfileActivity.this);
        Profile_tool=(Toolbar)findViewById(R.id.profile_toolbar);
        setSupportActionBar(Profile_tool);
        getSupportActionBar().setTitle("User_Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user_key=(String)getIntent().getExtras().getString("Blog_id");
        mCurrent_state="not_friends";
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mRootRef=FirebaseDatabase.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             /*       PD.setTitle("Please wait");
                    PD.setMessage("required files are downloading");
                    PD.setCanceledOnTouchOutside(false);
                    PD.setIndeterminate(false);
                    PD.setCancelable(true);
                    PD.show();
                    */

                Name=dataSnapshot.child(user_key).child("name").getValue().toString();
                String Status=dataSnapshot.child(user_key).child("status").getValue().toString();
                String image=dataSnapshot.child(user_key).child("image").getValue().toString();
                mName.setText(Name);
                mStatus.setText(Status);
               if(!image.equals("default")) {
                    Picasso.with(getApplicationContext()).load(image).placeholder(R.mipmap.ic_account_circle_white_48dp).into(Profile_image);
                }
                mFriendReqDatabase.child(current_user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       // Toast.makeText(getApplicationContext(),"executing this", Toast.LENGTH_SHORT).show();
                        if (dataSnapshot.hasChild(user_key)) {
                            String req_type = dataSnapshot.child(user_key).child("request_type").getValue().toString();
                           // Toast.makeText(getApplicationContext(),req_type + "  "+mCurrent_state, Toast.LENGTH_SHORT).show();
                            if (req_type.equals("received")) {
                                mCurrent_state ="request_received";
                                mSendRequestBtn.setText("Accept Friend Request");
                                mProfileCancelRequestBtn.setVisibility(View.VISIBLE);
                                mProfileCancelRequestBtn.setEnabled(true);
                            } else if (req_type.equals("sent")) {
                                mCurrent_state ="req_sent";
                                mSendRequestBtn.setText("cancel Friend Request");
                                mProfileCancelRequestBtn.setVisibility(View.INVISIBLE);
                                mProfileCancelRequestBtn.setEnabled(false);

                            } else if(req_type.equals("friends")){
                                mSendRequestBtn.setText("Unfriend "+Name);
                                mCurrent_state="friends";
                                mProfileCancelRequestBtn.setVisibility(View.INVISIBLE);
                                mProfileCancelRequestBtn.setEnabled(false);
                            }
                            else{
                                mCurrent_state="not_friends";
                                mSendRequestBtn.setText("Send Friend request");
                                mProfileCancelRequestBtn.setVisibility(View.INVISIBLE);
                                mProfileCancelRequestBtn.setEnabled(false);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if(mCurrent_state.equals("not_friends")){
                    mProfileCancelRequestBtn.setVisibility(View.INVISIBLE);
                    mProfileCancelRequestBtn.setEnabled(false);
                }
             /* if (PD.isShowing()) {
                    PD.dismiss();
                } */
                //Toast.makeText(getApplicationContext(),user_key, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendRequestBtn.setEnabled(false);
              //  Toast.makeText(getApplicationContext(),"Button is functioning", Toast.LENGTH_SHORT).show();
                    if (mCurrent_state.equals("not_friends")) {
                        mFriendReqDatabase.child(current_user).child(user_key).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mFriendReqDatabase.child(user_key).child(current_user).child("request_type")
                                            .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> mtask) {
                                            if (mtask.isSuccessful()) {
                                                HashMap<String,String> notificationData=new HashMap<>();
                                                notificationData.put("from",current_user);
                                                notificationData.put("type","request");
                                                mNotificationDatabase.child(user_key).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mSendRequestBtn.setEnabled(true);
                                                        mCurrent_state = "req_sent";
                                                        mSendRequestBtn.setText("cancel Friend Request");
                                                        mProfileCancelRequestBtn.setVisibility(View.INVISIBLE);
                                                        mProfileCancelRequestBtn.setEnabled(false);
                                                        Toast.makeText(getApplicationContext(), "Request sent successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        }
                                    });
                                } else {
                                    mSendRequestBtn.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), "failed to send request", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                else if(mCurrent_state.equals("req_sent")){
                    mFriendReqDatabase.child(current_user).child(user_key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_key).child(current_user).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void bVoid) {
                                    mSendRequestBtn.setEnabled(true);
                                    mCurrent_state="not_friends";
                                    mSendRequestBtn.setText("send Friend request");
                                    mProfileCancelRequestBtn.setVisibility(View.INVISIBLE);
                                    mProfileCancelRequestBtn.setEnabled(false);
                                    Toast.makeText(getApplicationContext(),"Request cancelled successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
                else if(mCurrent_state.equals("request_received")){
                   // mFriendReqDatabase.child(current_user).child(user_key).
                    mCurrent_state="friend";
                    final String current_date= DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(current_user).child(user_key).child("date").setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_key).child(current_user).child("date").setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void bVoid) {
                                    mSendRequestBtn.setEnabled(true);
                                    mProfileCancelRequestBtn.setVisibility(View.INVISIBLE);
                                    mProfileCancelRequestBtn.setEnabled(false);
                                    mSendRequestBtn.setText("UnFriend "+Name);
                                    mCurrent_state="friends";
                                    mFriendReqDatabase.child(current_user).child(user_key).child("request_type").setValue("friends");
                                    mFriendReqDatabase.child(user_key).child(current_user).child("request_type").setValue("friends");
                                    mRootRef.child("seenMsgs").child(current_user).child(user_key).child("seen").setValue("0");
                                    mRootRef.child("seenMsgs").child(current_user).child(user_key).child("watch").setValue("0");
                                }
                            });

                        }
                    });
                }
                else if(mCurrent_state.equals("friends")){
                        mFriendReqDatabase.child(current_user).child(user_key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendReqDatabase.child(user_key).child(current_user).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void bVoid) {
                                        mSendRequestBtn.setEnabled(true);
                                        mCurrent_state = "not_friends";
                                        mSendRequestBtn.setText("send Friend request");
                                        mProfileCancelRequestBtn.setVisibility(View.INVISIBLE);
                                        mProfileCancelRequestBtn.setEnabled(false);
                                        Toast.makeText(getApplicationContext(), "Request cancelled successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                else {
                    mSendRequestBtn.setEnabled(true);
                }

            }

        });
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
