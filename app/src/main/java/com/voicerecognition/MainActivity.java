package com.voicerecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {
    static DatabaseReference dbr ;
    Button addnewprofile,identifysamples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbr = FirebaseDatabase.getInstance().getReference().child("voicerecog");
        addnewprofile = (Button) findViewById(R.id.addnewprofile);
        identifysamples = (Button) findViewById(R.id.identifysamples);

        addnewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NewProfileListActivity.class));
            }
        });
        identifysamples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,IdentifyActivity.class));
            }
        });
    }

    public void toNewProfile(View view){
    }

    @Override
    protected void onResume() {
        super.onResume();
         checkFirebase(dbr,MainActivity.this);
    }

    public boolean checkFirebase(DatabaseReference db,final Context cont){
        Boolean b = false;
        if (SplashActivity.isOnline(this)) {
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        String a = dataSnapshot.getValue().toString();
                        System.out.println("datasnap val="+a);
                    }catch (NullPointerException e){
                        Toast.makeText(MainActivity.this, "Server error, please contact admin.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(cont,SplashActivity.class));
                        finish();
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


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Press home to exit.", Toast.LENGTH_SHORT).show();
    }
}
