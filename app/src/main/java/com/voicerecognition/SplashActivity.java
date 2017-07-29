package com.voicerecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.Manifest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends Activity {
    static DatabaseReference dbr;
    static Boolean b = false;
    static Context contx;
    Button toMainAct;
    private static final String[] STORAGE_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    MarshmallowPermissions mr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dbr = FirebaseDatabase.getInstance().getReference().child("voicerecog");
        contx = SplashActivity.this;
        mr = new MarshmallowPermissions(this);
        mr.reguestNewPermissions(this,STORAGE_PERMISSION);
        toMainAct = (Button) findViewById(R.id.toMainAct);
        toMainAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFirebase(dbr,SplashActivity.this);
            }
        });
    }

    public boolean checkFirebase(DatabaseReference db,Context cont){
        if (isOnline(contx)) {
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        String a = dataSnapshot.getValue().toString();
                        System.out.println("datasnap val="+a);
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }catch (NullPointerException e){
                        Toast.makeText(SplashActivity.this, "Server error, please contact admin.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(cont, "Please make sure you are connected to the internet and try again.", Toast.LENGTH_SHORT).show();
        }
        return b;
    }

    public static boolean isOnline(Context cont) {
        ConnectivityManager cm = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }



}
