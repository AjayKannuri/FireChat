package com.example.hp.firechat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser,mCurrentUser;
    private Toolbar mToolbar;
    private SectionPageAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTablayout;
   private DatabaseReference mDatabase;
   //private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       try {
            setContentView(R.layout.activity_main);
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser=mAuth.getCurrentUser();
            mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Fire Chat");
            if(mCurrentUser!=null) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            }
            //getting the tabs
            mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
            mSectionPagerAdapter = new SectionPageAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionPagerAdapter);
            mTablayout = (TabLayout) findViewById(R.id.main_tabs);
            mTablayout.setupWithViewPager(mViewPager);
           // threadBro();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        CurrentUser=mAuth.getCurrentUser();
        if(CurrentUser==null){
            Intent loginIntent = new Intent(MainActivity.this, StartActivity.class);
            //     Toast.makeText(getApplicationContext(),"going to loginactivity", Toast.LENGTH_SHORT).show();
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
       else{
           // Toast.makeText(getApplicationContext(),"there is some person who logged in", Toast.LENGTH_SHORT).show();
            try {
                 mDatabase.child(CurrentUser.getUid()).child("online").setValue("true");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null) {
            mDatabase.child(CurrentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
           // mDatabase.child(CurrentUser.getUid()).child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }
    } */

     @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);


         return true;
    }
     @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         if (item.getItemId() == R.id.settings_btn) {
            //Toast.makeText(getApplicationContext(),"entered", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(MainActivity.this,Post_activity.class));
            try {
                Intent RegisterIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(RegisterIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (item.getItemId() == R.id.users_btn) {
            Intent UsersIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(UsersIntent);
        }
        if (item.getItemId() == R.id.main_logout_btn) {
            mAuth.signOut();
            mDatabase.child(CurrentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
            Intent loginIntent = new Intent(MainActivity.this, StartActivity.class);
            //     Toast.makeText(getApplicationContext(),"going to loginactivity", Toast.LENGTH_SHORT).show();
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void threadBro()
    {

        Runnable runnable = new Runnable() {
            public void run() {
               // Toast.makeText(getApplicationContext(),"thread is running ra", Toast.LENGTH_SHORT).show();
                long endTime = System.currentTimeMillis()
                        + 20*1000;

                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            Log.d("thread","thread is executing");
                            wait(endTime -
                                    System.currentTimeMillis());
                        } catch (Exception e) {}
                    }
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.e("ajay1","wow1");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("ajay2","wow2");
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null) {
            mDatabase.child(CurrentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
            // mDatabase.child(CurrentUser.getUid()).child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }
}
class Task implements Runnable{
    public void run(){
        for(int i=0;i<2;i++)
        Log.d("thread1","thread is executing");
    }
}
class MyAndroidThread implements Runnable
{
    Activity activity;
    public MyAndroidThread(Activity activity)
    {
        this.activity = activity;
    }
    @Override
    public void run()
    {
        /*activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {


            }
        });*/
        long endTime = System.currentTimeMillis()
                + 5*1000;

        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    Log.d("thread","thread is executing");
                   // Toast.makeText(activity,"thread is running in class with correct time", Toast.LENGTH_SHORT).show();

                   // Thread.sleep(endTime-System.currentTimeMillis());
                     wait(endTime -
                            System.currentTimeMillis());
                   // Toast.makeText(activity,"thread is running in class with correct time", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {}
            }
        }
    }

}
