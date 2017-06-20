package com.d4vinci.chatapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.ResultCodes
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123

    var savedState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedState = savedInstanceState
        setContentView(R.layout.activity_main)

        var btStart = findViewById(R.id.btStart)
        btStart.setOnClickListener {
            stopService(Intent(this, RadioService::class.java))
            startService(Intent(this, RadioService::class.java)) }
        var btStop = findViewById(R.id.btStop)
        btStop.setOnClickListener { stopService(Intent(this, RadioService::class.java)) }

        if (FirebaseAuth.getInstance().currentUser != null) {
            //already signed in
            loadMainFragment()
        } else {
            //not signed in
            logInUser()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }
    }

    fun loadMainFragment() {
        if (findViewById(R.id.container_activity_main) != null) {
            if (savedState != null) {
                return
            }
            val mainFragment = MainFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container_activity_main, mainFragment)
                    .commit()
        }
    }

    private fun logInUser() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                ))
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN)
    }

    private fun handleSignInResponse(resultCode: Int, data: Intent?) {
        val response = IdpResponse.fromResultIntent(data)

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            loadMainFragment()
            return
        } else {
            // Sign in failed
            if (response == null) {
                logInUser()
                return
            }

            if (response.errorCode == ErrorCodes.NO_NETWORK) {
                finish()
                return
            }

            if (response.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                finish()
                return
            }
        }
    }
}
