package com.apiguave.tinderclonecompose.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.apiguave.tinderclonecompose.BuildConfig
import com.apiguave.tinderclonecompose.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    companion object {
        private val TAG = "MainActivity"
        private const val REQUEST_LOCATION_PERMISSION = 101
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Main activity created")

        // Initialize Firebase
        Firebase.initialize(context = this)
        Log.d(TAG,"Firebase initialized")

        // Install appropriate App Check provider based on build type
        if (BuildConfig.DEBUG) {
            Log.d(TAG,"Debug mode") // generates the Firebase debug token (valid for 1 hr) which needs to be added to Firebase AppCheck
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            Log.d(TAG,"Release mode")
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    Firebase.appCheck.installAppCheckProviderFactory(
                        PlayIntegrityAppCheckProviderFactory.getInstance()
                    )
                    Log.d(TAG, "Play Integrity AppCheck provider installed successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error installing Play Integrity AppCheck provider", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error installing Play Integrity AppCheck provider",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Request location permissions
        requestLocationPermissions()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set content for the activity
        setContent { MainContent(googleSignInClient) }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Log.d(TAG, "Location permissions granted")
                } else {
                    Log.d(TAG, "Location permissions denied")
                    Toast.makeText(this, "Location permissions denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
