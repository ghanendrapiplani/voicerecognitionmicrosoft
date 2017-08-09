package com.voicerecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
 

public class MainActivity extends Activity {
    
    Button addnewprofile,identifysamples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Press home to exit.", Toast.LENGTH_SHORT).show();
    }
}
