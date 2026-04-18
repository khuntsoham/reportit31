package com.example.reportit31

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // This is your Splash/Welcome Layout
        
        auth = FirebaseAuth.getInstance()
        UIUtils.hideNavigationBar(this)

        // Delay for Splash effect (e.g., 2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, 2000)
    }

    private fun checkSession() {
        if (auth.currentUser != null) {
            // User is already logged in, go to Home
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            // No user logged in, go to Login
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Close Splash Activity
    }
}
