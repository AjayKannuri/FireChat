package com.example.hp.firechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private Button mRegBtn;
    private Button mLoginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRegBtn=(Button)findViewById(R.id.Start_reg);
        mLoginBtn=(Button)findViewById(R.id.LoginBtn);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Reg_Intent = new Intent(StartActivity.this, RegisterActivity.class);
                //     Toast.makeText(getApplicationContext(),"going to loginactivity", Toast.LENGTH_SHORT).show();
               // Reg_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Reg_Intent);
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Login_Intent = new Intent(StartActivity.this, LoginActivity.class);
                //     Toast.makeText(getApplicationContext(),"going to loginactivity", Toast.LENGTH_SHORT).show();
                // Reg_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Login_Intent);
            }
        });
    }
}
