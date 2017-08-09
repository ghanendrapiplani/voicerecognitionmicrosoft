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


import java.util.ArrayList;

/**
 * Created by ghanendra on 29/07/2017.
 */

public class NewProfileListActivity extends Activity {
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

    public void registerNewUser(View view){
        startActivity(new Intent(this,NewProfileAddActivity.class));
    }
}
