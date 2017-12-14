package com.example.hp.firechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.TimeZoneFormat;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {
    DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private Button changeStatusBtn;
    private Button changeImageBtn;
    boolean DEVELOPER_MODE=true;
    private Toolbar settings_Tool_bar;
    private static int Gallery_request=1;
    private StorageReference mImageStorage;
    private DatabaseReference mDatabase;
    private static int MAX_LENGTH=30;
    private ProgressDialog PD;
    Uri mImageUri;
   // private Bitmap compressedImageBitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDisplayImage=(CircleImageView)findViewById(R.id.user_single_image);
        mName=(TextView)findViewById(R.id.settings_name);
        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        changeStatusBtn=(Button)findViewById(R.id.Settings_status_Btn);
        mStatus=(TextView)findViewById(R.id.Settings_status);
        settings_Tool_bar=(Toolbar) findViewById(R.id.settings_tool_bar);
        changeImageBtn=(Button)findViewById(R.id.settings_imageBtn);
        PD = new ProgressDialog(SettingsActivity.this);
        mImageStorage= FirebaseStorage.getInstance().getReference().child("profile_pics");
        setSupportActionBar(settings_Tool_bar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        try {
        String uid=mAuth.getCurrentUser().getUid();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

           /* String timeStamp = TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis()) + "";
            Log.d("time ra","aja "+timeStamp);
            Log.d("time ra2","aja "+ currentTimeMillis());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Date date = new Date(timestamp.getTime());*/

            // S is the millisecond
           // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");

           // Date date = new Date();
           // java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
            //Log.d("Time: " ,"aja "+ dateFormat.format(date));
            String  currentDateTimeString = DateFormat.getDateTimeInstance()
                    .format(new Date());
            Log.d("Time: " ,"aja "+ currentDateTimeString);
            Date currentTime = Calendar.getInstance().getTime();
            Log.d("time 2","aja"+currentTime);
            java.util.Date today = new java.util.Date();
           // System.out.println(new java.sql.Timestamp(today.getTime()));
            Log.d("time 3","aja "+new java.sql.Timestamp(today.getTime()));
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = dateFormat.parse(today.toString());
                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                Log.d("time 4","ajay "+timestamp);
            } catch(Exception e) {
                e.printStackTrace();
            }
            long unixSeconds = 1372339860;
            //Date date = new Date(unixSeconds*1000L); // *1000 is to convert seconds to milliseconds
            Date date = new Date( System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+05:30")); // give a timezone reference for formating (see comment at the bottom
            String formattedDate = sdf.format(date);
           // System.out.println(formattedDate);
            Log.d("time 5","ajay2 "+formattedDate);
            Log.d("time 6","ajay "+System.currentTimeMillis());
           //Long unixTime = sdf.parse("05:30").getTime();
            //unixTime = unixTime / 1000;
            Date date2=new Date();
            Log.d("time 7","ajay "+new Date().getTime());
            Log.d("time 8","ajay "+date.getTime());
            //DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date3 = dateFormat.parse(formattedDate);
            long unixTime3 = (long)date3.getTime();
            Log.d("time 9","ajay "+unixTime3);
            Date date4=new Date(date3.getTime());
            Log.d("time 10","ajay "+date4);


            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   // Toast.makeText(getApplicationContext(),"pd is about to start", Toast.LENGTH_SHORT).show();
                      //  PD = new ProgressDialog(SettingsActivity.this);
                       /* PD.setTitle("Please wait");
                        PD.setMessage("required files are downloading");
                        PD.setCanceledOnTouchOutside(false);
                        PD.setIndeterminate(false);
                        PD.setCancelable(true);
                        PD.show(); */
                    String name = (String)dataSnapshot.child("name").getValue();
                    final String image = (String)dataSnapshot.child("image").getValue();
                    String status = (String)dataSnapshot.child("status").getValue();
                  //  String thumb_image =(String) dataSnapshot.child("thumb_image").getValue();
                    mName.setText(name);
                    mStatus.setText(status);
                    if(!image.equals("default")) {
                        Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mDisplayImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(SettingsActivity.this).load(image).placeholder(R.mipmap.ic_account_circle_white_48dp).into(mDisplayImage);
                            }
                        });
                    }
                  /*  if (PD.isShowing()) {
                        PD.dismiss();
                    } */
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        changeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StatusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                Toast.makeText(getApplicationContext(),"going to StatusActivity", Toast.LENGTH_SHORT).show();
               // StatusIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(StatusIntent);
            }
        });
        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(galleryIntent, Gallery_request);
               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        }catch (Exception e){
            e.printStackTrace();
        }
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

    }
    /*@Override
    protected void onStart() {
        super.onStart();
        mDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue("true");
    }
    @Override
    protected void onStop() {
        super.onStop();
        mDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue("false");
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Gallery_request && resultCode == RESULT_OK) {    //String imageUri=data.getDataString();
                try {
                    Uri imageUri = data.getData();
                    mImageUri = imageUri;
                    // start picker to get image for cropping and then use the image in cropping activity
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .setMinCropWindowSize(400, 400)   //crop only which are greater than 400px
                            .start(SettingsActivity.this);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                  //  PD.setTitle("uploading image");
                  /*  PD.setMessage("please wait while we upload the image");
                    PD.setCanceledOnTouchOutside(false);
                    PD.setIndeterminate(false);
                    PD.setCancelable(true);
                    PD.show(); */
                    final Uri resultUri = result.getUri();

                    //  File thumb_filepath = new File(resultUri.getPath());
                    String current_user = mAuth.getCurrentUser().getUid();
                    StorageReference filepath = mImageStorage.child(current_user + ".jpg");

             /*   try {
                    compressedImageBitmap = new Compressor(this)
                            .setMaxWidth(400)
                            .setMaxHeight(400)           //cropping to 200px
                            .setQuality(80)               //setting quality to 75%
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                } */
                    //  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    //final byte[] thumb_byte=baos.toByteArray();

                    // final StorageReference thumbFilepath=mImageStorage.child("thumbs").child(current_user + ".jpg");

                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                String download_uri = task.getResult().getDownloadUrl().toString();
                           /* UploadTask uploadTask=thumbFilepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadtask) {
                                    if(uploadtask.isSuccessful()){
                                        String download_thumb_uri=uploadtask.getResult().getDownloadUrl().toString();
                                        mUserDatabase.child("thumb_image").setValue(download_thumb_uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> thumb_task) {
                                                if(!thumb_task.isSuccessful()){
                                                    if (PD.isShowing()) {
                                                        PD.dismiss();
                                                    }
                                                    Toast.makeText(getApplicationContext(),"uploading thumbnail1 is failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        if (PD.isShowing()) {
                                            PD.dismiss();
                                        }
                                        Toast.makeText(getApplicationContext(),"uploading thumbnail2 is failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }); */
                                mUserDatabase.child("image").setValue(download_uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mUserDatabase.child("thumb_image").setValue("default").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mDisplayImage.setImageURI(resultUri);
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    });
                /*if(resultUri!=null) {
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                String download_uri = task.getResult().getDownloadUrl().toString();
                                mUserDatabase.child("image").setValue(download_uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mDisplayImage.setImageURI(resultUri);

                                        }
                                    }
                                });
                            }
                        }
                    });
                }*/

                  /*  if(PD.isShowing()) {
                        PD.dismiss();
                    } */
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
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
