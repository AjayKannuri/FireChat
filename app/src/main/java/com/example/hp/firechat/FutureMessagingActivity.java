package com.example.hp.firechat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FutureMessagingActivity extends AppCompatActivity {
    private Toolbar status_Tool;
    private EditText DateText;
    private EditText TimeText;
    private EditText MessageText;
    private Button OkayBtn;
    private String mChatUser;
    private String mChatUsername;
    private DatabaseReference mMessagesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_messaging);
        status_Tool=(Toolbar) findViewById(R.id.future_app_toolbar);
        setSupportActionBar(status_Tool);
        getSupportActionBar().setTitle("Change status");
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DateText=(EditText)findViewById(R.id.Date_text);
        TimeText=(EditText)findViewById(R.id.Time_text);
        OkayBtn=(Button)findViewById(R.id.okay_button);
        MessageText=(EditText)findViewById(R.id.future_Message);
        mChatUser = getIntent().getStringExtra("current_user");
        mChatUsername = getIntent().getStringExtra("user_key");
        mMessagesDatabase= FirebaseDatabase.getInstance().getReference().child("FutureMessages");
        OkayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String Date = DateText.getText().toString();
                    String Time = TimeText.getText().toString();
                    final String message = MessageText.getText().toString();
                    if (!TextUtils.isEmpty(Date) && !TextUtils.isEmpty(Time) && !TextUtils.isEmpty(message)) {
                        if (Date.length() == 10 && Time.length() == 8) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            java.util.Date date3 = null;
                            try {
                                date3 = dateFormat.parse(Date + " " + Time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            final long unixTime3 = (long) date3.getTime();
                            Log.e("database", "ajay" + unixTime3);
                            Log.e("database", "vijay" + System.currentTimeMillis());


                            mMessagesDatabase.child(mChatUsername).child(mChatUser).child("type").setValue("going").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mMessagesDatabase.child(mChatUsername).child(mChatUser).child("message").setValue(message);
                                        mMessagesDatabase.child(mChatUsername).child(mChatUser).child("ftimestamp").setValue(unixTime3);
                                        mMessagesDatabase.child(mChatUsername).child(mChatUser).child("timestamp").setValue(System.currentTimeMillis());
                                        mMessagesDatabase.child(mChatUser).child(mChatUsername).child("timestamp").setValue(System.currentTimeMillis());
                                        mMessagesDatabase.child(mChatUser).child(mChatUsername).child("type").setValue("coming");
                                        mMessagesDatabase.child(mChatUser).child(mChatUsername).child("message").setValue(message);
                                        mMessagesDatabase.child(mChatUser).child(mChatUsername).child("ftimestamp").setValue(unixTime3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "failed to add it ", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Added to the database ", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }

                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "please enter correct inputs", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter the fields", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }
    @Override

    protected void onResume() {

        super.onResume();//visible

        Log.d("resume11","Activity resumed");

    }

    @Override

    protected void onPause() {

        super.onPause();//invisible

        Log.d("resume12", "Activity paused");
    }
}
