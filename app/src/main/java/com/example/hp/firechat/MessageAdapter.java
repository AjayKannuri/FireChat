package com.example.hp.firechat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp on 11/23/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private FirebaseUser current_user;
    private DatabaseReference mUsers;

    public MessageAdapter(List<Messages> mMessageList){
       // mAuth=FirebaseAuth.getInstance();
        this.mMessageList=mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        try {
            mAuth = FirebaseAuth.getInstance();
            current_user = mAuth.getCurrentUser();
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int position) {
        String current_user_id=mAuth.getCurrentUser().getUid();
        Messages c= mMessageList.get(position);
        mUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        String from_user=c.getFrom();
       // viewHolder.settings(from_user,current_user_id);
        viewHolder.mMessageText.setText(c.getMessage());

            try {
                mUsers.child(from_user).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.mMessageName.setText(dataSnapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
                if (current_user_id.equals(from_user)) {
                    viewHolder.mMessageText.setBackgroundColor(Color.WHITE);
                    //  Toast.makeText(getClass(),"uploading thumbnail2 is failed", Toast.LENGTH_SHORT).show();
                    Log.d("one", "current user id");
                    viewHolder.mMessageText.setTextColor(Color.BLACK);
                } else {
                    viewHolder.mMessageText.setBackgroundResource(R.drawable.message_text_background);
                    viewHolder.mMessageText.setTextColor(Color.WHITE);

                }

        }catch (Exception e){
            e.printStackTrace();
        }
        //viewHolder.profileImage.setText(c.getTime());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView mMessageText;
        public TextView mMessageName;
        public CircleImageView profileImage;
        public MessageViewHolder(View view) {
            super(view);
            mMessageText=(TextView)view.findViewById(R.id.message_text_layout);
            profileImage=(CircleImageView)view.findViewById(R.id.message_profile_layout);
            mMessageName=(TextView)view.findViewById(R.id.message_name_layout);

        }
      /*  public void settings(String from_user,String current_user_id ){
            try {
                mUsers.child(from_user).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mMessageName.setText(dataSnapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {

                if (current_user_id.equals(from_user)) {
                    mMessageText.setBackgroundColor(Color.WHITE);
                    //  Toast.makeText(getClass(),"uploading thumbnail2 is failed", Toast.LENGTH_SHORT).show();
                    Log.d("one", "current user id");
                    mMessageText.setTextColor(Color.BLACK);
                } else {
                    mMessageText.setBackgroundResource(R.drawable.message_text_background);
                    mMessageText.setTextColor(Color.WHITE);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        } */
    }

}
