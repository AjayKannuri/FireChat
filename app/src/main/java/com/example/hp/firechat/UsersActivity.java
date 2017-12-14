package com.example.hp.firechat;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.w3c.dom.Text;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mToolbar=(Toolbar)findViewById(R.id.users_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsersList=(RecyclerView)findViewById(R.id.users_list);
        mAuth=FirebaseAuth.getInstance();
        mUsersDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(UsersActivity.this));

    }
    @Override
    protected void onStart(){
        super.onStart();
      //  FirebaseUser currentUser=mAuth.getCurrentUser();
     //   if(currentUser!=null) {
            //mUsersDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");
      //  }
        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {
                final String user_key = getRef(position).getKey();
                usersViewHolder.setName(users.getName());
                usersViewHolder.setImage(getApplicationContext(),users.getImage());
                usersViewHolder.setStatus(users.getStatus());
                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Intent Profile_Intent = new Intent(UsersActivity.this, ProfileActivity.class);
                        Profile_Intent.putExtra("Blog_id",user_key);
                        // StatusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(Profile_Intent);
                        Toast.makeText(getApplicationContext(),"going to profile Activity", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),user_key, Toast.LENGTH_SHORT).show();

                    }
                });

            }

        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
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
        public void setImage(final Context ctx, final String image){
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
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.e("ajay123","wow1");
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null) {
            mUsersDatabase.child(currentUser.getUid()).child("online").setValue("true");
            // mDatabase.child(CurrentUser.getUid()).child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ajay124","wow2");
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null) {
            mUsersDatabase.child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
            // mDatabase.child(CurrentUser.getUid()).child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }
}
