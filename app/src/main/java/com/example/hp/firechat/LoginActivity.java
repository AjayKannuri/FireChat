package com.example.hp.firechat;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mLoginEmail;
    private EditText mLoginPassword;
    private Button mSignUpbtn;
    private FirebaseAuth mAuth;
    private ProgressDialog PD;
    private DatabaseReference mDatabaseUsers;
    private String current_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mToolbar=(Toolbar)findViewById(R.id.Login_toolbar);
        mLoginEmail=(EditText)findViewById(R.id.emailField);
        mLoginPassword=(EditText)findViewById(R.id.passwordField);
        mSignUpbtn=(Button)findViewById(R.id.SignUpBtn);
        PD = new ProgressDialog(LoginActivity.this);
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSignUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mLoginEmail.getText().toString().trim();
                String password=mLoginPassword.getText().toString().trim();

                if(!TextUtils.isEmpty(email)&&(!TextUtils.isEmpty(password)))
                {
                    PD.setMessage("Starting signing in");
                    PD.setCanceledOnTouchOutside(false);
                    PD.setIndeterminate(false);
                    PD.setCancelable(true);
                    PD.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                current_user=mAuth.getCurrentUser().getUid();
                                mDatabaseUsers.child(current_user).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"LOGGIN", Toast.LENGTH_SHORT).show();
                                        Intent mainIntent =new Intent(LoginActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        if(PD.isShowing()){
                                            PD.dismiss();
                                        }
                                    }
                                });

                            }else{
                                Toast.makeText(getApplicationContext(),"ERROR LOGIN", Toast.LENGTH_SHORT).show();
                                if(PD.isShowing()){
                                    PD.dismiss();
                                }
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"PLEASE ENTER THE FIELDS", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
