package com.example.hp.firechat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private RecyclerView mRequestsList;
    private View mMainView;
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private DatabaseReference mRequestsReference;
    private DatabaseReference mUsersDatabase;
    private Query mRequestsQuery;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView= inflater.inflate(R.layout.fragment_requests, container, false);
        mRequestsList=(RecyclerView)mMainView.findViewById(R.id.requests_list);
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser().getUid();
        mRequestsReference= FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrentUser);
        mRequestsQuery= FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrentUser).orderByChild("request_type").equalTo("received");
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mRequestsQuery.keepSynced(true);
        mRequestsReference.keepSynced(true);
        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }
    @Override
    public   void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests, RequestsFragment.RequestsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestsFragment.RequestsViewHolder>(
                Requests.class,
                R.layout.users_single_layout,
                RequestsFragment.RequestsViewHolder.class,
                mRequestsQuery
        ) {
            @Override
            protected void populateViewHolder(final RequestsFragment.RequestsViewHolder RequestsViewHolder, Requests requests, int position) {
                final String user_key = getRef(position).getKey();
                mUsersDatabase.child(user_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name=dataSnapshot.child("name").getValue().toString();
                        String status=dataSnapshot.child("status").getValue().toString();
                        String image=dataSnapshot.child("image").getValue().toString();
                        RequestsViewHolder.setThings(getContext(),name,status,image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                RequestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent Profile_Intent = new Intent(getContext(), ProfileActivity.class);
                        Profile_Intent.putExtra("Blog_id",user_key);
                        // StatusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(Profile_Intent);
                        Toast.makeText(getContext(),"going to profile Activity", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),user_key, Toast.LENGTH_SHORT).show();
                    }
                });


            }

        };
        mRequestsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private CircleImageView mDisplayImage;
        public RequestsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }
        public void setThings(final Context ctx, String name, String status, final String image){
            TextView userStatusView=(TextView)mView.findViewById(R.id.user_single_status);
            mDisplayImage=(CircleImageView)mView.findViewById(R.id.user_single_image);
            TextView userNameView=(TextView)mView.findViewById(R.id.user_single_name);
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
            userStatusView.setText(status);
            userNameView.setText(name);
        }
    }

}

