package com.example.hp.firechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity {
    private Button changeBtn;
    private EditText status;
    private Toolbar status_Tool;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog PD;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        changeBtn=(Button)findViewById(R.id.changeBtn);
        status=(EditText)findViewById(R.id.Date_text);
        status_Tool=(Toolbar) findViewById(R.id.status_toolbar);
        setSupportActionBar(status_Tool);
        getSupportActionBar().setTitle("Change status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
         uid=mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String statusBro=dataSnapshot.child(uid).child("status").getValue().toString();
                status.setText(statusBro);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_changed = status.getText().toString();
                if(!TextUtils.isEmpty(status_changed)) {
                         /*   if(PD == null){
                                PD = new ProgressDialog(StatusActivity.this);
                                PD.setMessage("please wait..");
                                PD.setIndeterminate(false);
                                PD.setCancelable(true);
                                PD.show();
                            } */
                    mDatabase.child(uid).child("status").setValue(status_changed);
                    Intent SettingsIntent = new Intent(StatusActivity.this, SettingsActivity.class);
                    // SettingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // SettingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(SettingsIntent);
                    Toast.makeText(getApplicationContext(), "status is updated", Toast.LENGTH_SHORT).show();
                          /*  if(PD.isShowing()){
                                PD.dismiss();
                            } */
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter the field", Toast.LENGTH_SHORT).show();
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
