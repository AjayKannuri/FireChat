package com.example.hp.firechat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {
    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    public  DatabaseReference mRootRef;
    private  FirebaseAuth mAuth;
    private String mCurrentUser;
    private View mMainView;
    private DatabaseReference mUsersDatabase;
    private String name;
    private String status;
    private String image;
    private String onlineStatus;
    private Query basedOnTime;
    private int watch2;
    private int numSeen;
    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView=inflater.inflate(R.layout.fragment_friends,container,false);
        mFriendsList=(RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser().getUid();
        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser);
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRootRef.child("seenMsgs").keepSynced(true);
        basedOnTime=FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrentUser).orderByChild("time").startAt(System.currentTimeMillis()).endAt(System.currentTimeMillis()-86400000L);
       // Toast.makeText(getContext(),"its working bro", Toast.LENGTH_SHORT).show();

        // Inflate the layout for this fragment
        return mMainView;
    }
    @Override
    public   void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsFragment.FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsFragment.FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsFragment.FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int position) {
                 final String user_key = getRef(position).getKey();
                friendsViewHolder.setDate(friends.getDate());
                mUsersDatabase.child(user_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         name=dataSnapshot.child("name").getValue().toString();
                         status=dataSnapshot.child("status").getValue().toString();
                         //image=dataSnapshot.child("thumb_image").getValue().toString();
                        image=dataSnapshot.child("image").getValue().toString();
                         if(dataSnapshot.hasChild("online")) {
                             onlineStatus = dataSnapshot.child("online").getValue().toString();
                             friendsViewHolder.setUserOnline(onlineStatus);
                         }

                         friendsViewHolder.setThings(getContext(),name,status,image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRootRef.child("seenMsgs").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       // Toast.makeText(getContext(),"coming here to check", Toast.LENGTH_SHORT).show();
                        if(dataSnapshot.hasChild(user_key)){
                            String Seen=dataSnapshot.child(user_key).child("seen").getValue().toString();
                            numSeen=Integer.parseInt(Seen);
                           // Toast.makeText(getContext(),"okay1   "+numSeen, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mRootRef.child("Messages").child(mCurrentUser).child(user_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        long k = dataSnapshot1.getChildrenCount();
                        // int watch=((int)k-numSeen);
                      watch2=(int)k;
                        int watch=watch2-numSeen;
                        String watching=Integer.toString(watch);
                        mRootRef.child("seenMsgs").child(mCurrentUser).child(user_key).child("watch").setValue(watching).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("success","added successfully");
                            }
                        });
                     //   Toast.makeText(getContext(),"okay2   "+watch, Toast.LENGTH_SHORT).show();
                        friendsViewHolder.seenSettings(watch);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
              //  friendsViewHolder.seenSettings(user_key);
                friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[]=new CharSequence[]{"open profile","send message"};
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        builder.setTitle("select Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //click even for each item
                                if(which==0){
                                    Intent Profile_Intent = new Intent(getContext(), ProfileActivity.class);
                                    Profile_Intent.putExtra("Blog_id",user_key);
                                    // StatusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(Profile_Intent);
                                }
                                if(which==1){
                                    Intent Chat_Intent = new Intent(getContext(), GoodChatActivity.class);
                                    Chat_Intent.putExtra("Blog_id",user_key);
                                    Chat_Intent.putExtra("user_name",name);
                                    // StatusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(Chat_Intent);
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

        };
        mFriendsList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private CircleImageView mDisplayImage;
        private TextView seenNumberView;
        public FriendsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            mDisplayImage=(CircleImageView)mView.findViewById(R.id.user_single_image);
            seenNumberView=(TextView)mView.findViewById(R.id.user_single_seen);
        }
        public void setDate(String date){
            TextView userStatusView=(TextView)mView.findViewById(R.id.user_single_status);
            //Toast.makeText(ctx,"its working bro", Toast.LENGTH_SHORT).show();
            userStatusView.setText(date);
        }
        public void setThings(final Context ctx, String name, String status, final String image){
            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
            if(!image.equals("default")) {
                Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mDisplayImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ctx).load(image).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mDisplayImage);
                    }
                });
            }

        }
        public void setUserOnline(String onlineIcon){
            ImageView userOnlineView=(ImageView)mView.findViewById(R.id.online_Icon);
            if(onlineIcon.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

        public void seenSettings(int watch) {
            if(watch>0){
                String watching =Integer.toString(watch);
                seenNumberView.setText(watching);
               seenNumberView.setVisibility(View.VISIBLE);

            }
        }
    }
}

