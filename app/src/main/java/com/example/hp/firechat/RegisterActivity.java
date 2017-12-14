package com.example.hp.firechat;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateBtn;
    private ProgressDialog PD;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    FirebaseUser current_user;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            mDisplayName = (EditText) findViewById(R.id.nameField);
            mEmail = (EditText) findViewById(R.id.emailField);
            mPassword = (EditText) findViewById(R.id.passwordField);
            mCreateBtn = (Button) findViewById(R.id.registerBtn);
            mAuth = FirebaseAuth.getInstance();
            current_user = FirebaseAuth.getInstance().getCurrentUser();
            mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
            PD = new ProgressDialog(RegisterActivity.this);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Create Account");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCreateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final String name = mDisplayName.getText().toString().trim();
                        final String email = mEmail.getText().toString().trim();
                        final String password = mPassword.getText().toString().trim();
                        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                                PD.setMessage("signing up");
                                PD.setIndeterminate(false);
                                PD.setCanceledOnTouchOutside(false);
                                PD.setCancelable(true);
                                PD.show();

                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        current_user = FirebaseAuth.getInstance().getCurrentUser();
                                        if(current_user!=null) {
                                            String uid = current_user.getUid();
                                            String device_token= FirebaseInstanceId.getInstance().getToken();
                                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                            mDatabase.child("name").setValue(name);
                                            mDatabase.child("status").setValue("hi there I am using FireChat");
                                            mDatabase.child("image").setValue("default");
                                            mDatabase.child("thumb_image").setValue("default");
                                            mDatabase.child("device_token").setValue(device_token);
                                   /* HashMap<String, String> userMap = new HashMap<>();
                                    userMap.put("name", name);
                                    userMap.put("status", "hi there I am using FireChat");
                                    userMap.put("image", "default");
                                    userMap.put("thumb_image", "default");*/
                                            Toast.makeText(getApplicationContext(), "uploading is successful", Toast.LENGTH_SHORT).show();
                                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                            if (PD.isShowing()) {
                                                PD.dismiss();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "uploading is not successful", Toast.LENGTH_SHORT).show();
                                        if (PD.isShowing()) {
                                            PD.dismiss();
                                        }
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "PLEASE ENTER THE FIELDS", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
    }

}
