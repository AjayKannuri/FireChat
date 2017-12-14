package com.example.hp.firechat;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by hp on 11/18/2017.
 */

public class FireChat extends Application{
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String CurrentUser;
    private FirebaseUser mCurrentUser;
    public void onCreate() {
        try {
            super.onCreate();
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser=mAuth.getCurrentUser();
            if (!FirebaseApp.getApps(this).isEmpty()) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            }
            Picasso.Builder builder = new Picasso.Builder(this);
            builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(false);
            built.setLoggingEnabled(true);
            Picasso.setSingletonInstance(built);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(mCurrentUser!=null) {
            CurrentUser = mAuth.getCurrentUser().getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser);
            if (mAuth != null) {
                mUserDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                            Log.d("wow","onDisconnet is called");
                           // mUserDatabase.child("LastSeen").setValue(ServerValue.TIMESTAMP);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

}
