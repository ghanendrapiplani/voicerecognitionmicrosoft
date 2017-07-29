package com.voicerecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ghanendra on 29/07/2017.
 */

public class NewProfileListActivity extends Activity {
    static DatabaseReference dbr ;
    UserProfilesId uids;
    ArrayList<NewProfileModel> arr;
    ArrayList<String> arr2;
    FloatingActionButton fab;
    RecyclerView rv;
    NewProfileAdapter madapter;
    int count;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_newprofile);
        dbr = FirebaseDatabase.getInstance().getReference().child("voicerecog");
        uids=new UserProfilesId(this);
        arr= new ArrayList<>();
        arr2= new ArrayList<>();
        System.out.println("arr size oncreate"+arr.size());
        manageArrs();
        fab=(FloatingActionButton) findViewById(R.id.registerNewUser);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count<9)
                    startActivity(new Intent(NewProfileListActivity.this,NewProfileAddActivity.class));
                else
                    Toast.makeText(NewProfileListActivity.this, "Please clear users to start adding again. Limit 10.", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(this );
        mLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        rv = (RecyclerView) findViewById(R.id.rvlist);
        madapter=new NewProfileAdapter(arr,this);
        rv.setAdapter(madapter);
        rv.setLayoutManager(mLayoutManager2);
        madapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFirebase(dbr,NewProfileListActivity.this);
        manageArrs();
        madapter.notifyDataSetChanged();
        System.out.println("arraylist newprofilelistactivity onresume "+arr.size());
    }

    public void manageArrs(){
        for(NewProfileModel n : uids.getAllProfiles()){
            System.out.println("arr vals"+n.getUsername()+n.getIdentificationProfileId());
            count= uids.getAllProfiles().size();
            if(!arr2.contains(n.getIdentificationProfileId())){
                arr2.add(n.getIdentificationProfileId());
                arr.add(n);
            }
        }
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
                        Toast.makeText(cont, "Server error, please contact admin.", Toast.LENGTH_SHORT).show();
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

    public void registerNewUser(View view){
        startActivity(new Intent(this,NewProfileAddActivity.class));
    }
}
