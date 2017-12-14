package com.example.hp.firechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity3 extends AppCompatActivity {
    private String mChatUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mMessages;
    private String current_user_id;
    private RecyclerView mMessagesList;
    private LinearLayoutManager mLinearLayout;
    private DatabaseReference mUsersDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat3);
        mChatUser = getIntent().getStringExtra("Blog_id");
        current_user_id=mAuth.getCurrentUser().getUid();
        mMessages=FirebaseDatabase.getInstance().getReference().child("Messages").child(current_user_id).child(mChatUser);
        mMessagesList = (RecyclerView) findViewById(R.id.messages_list2);
        mLinearLayout = new LinearLayoutManager(ChatActivity3.this);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,UsersActivity.UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersActivity.UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersActivity.UsersViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersActivity.UsersViewHolder usersViewHolder, Users users, int position) {
                final String user_key = getRef(position).getKey();
                usersViewHolder.setName(users.getName());
                usersViewHolder.setImage(getApplicationContext(),users.getImage());
                usersViewHolder.setStatus(users.getStatus());
                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent Profile_Intent = new Intent(ChatActivity3.this, ProfileActivity.class);
                        Profile_Intent.putExtra("Blog_id",user_key);
                        // StatusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(Profile_Intent);
                        Toast.makeText(getApplicationContext(),"going to profile Activity", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),user_key, Toast.LENGTH_SHORT).show();

                    }
                });

            }

        };
        mMessagesList.setAdapter(firebaseRecyclerAdapter);
    }
    /* @Override
     protected void onStop() {
         super.onStop();
         mUsersDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue("false");
     }*/
    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        private TextView userNameView;
        private TextView userStatusView;
        private CircleImageView mDisplayImage;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mDisplayImage=(CircleImageView)mView.findViewById(R.id.user_single_image);
            userNameView=(TextView)mView.findViewById(R.id.user_single_name);
            userStatusView=(TextView)mView.findViewById(R.id.user_single_status);
        }
        public void setName(String name){
            userNameView.setText(name);
        }
        public void setStatus(String status){
            userStatusView.setText(status);
        }
        public void setImage(final Context ctx, String image){
            if(!image.equals("default")) {
                Picasso.with(ctx).load(image).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mDisplayImage);
            }
        }
    }
}
