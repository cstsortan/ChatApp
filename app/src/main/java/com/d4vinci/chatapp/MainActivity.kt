package com.d4vinci.chatapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainFragment = MainFragment.newInstance()
        if (findViewById(R.id.container_activity_main) != null) {
            if (savedInstanceState != null) {
                return
            }

            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container_activity_main, mainFragment)
                    .commit()
        }

    }
}
