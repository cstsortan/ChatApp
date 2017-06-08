package com.d4vinci.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment mainFragment = MainFragment.newInstance();
        if(findViewById(R.id.container_activity_main)!=null) {
            if(savedInstanceState!=null) {
                return;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_activity_main, mainFragment)
                    .commit();
        }

    }
}
