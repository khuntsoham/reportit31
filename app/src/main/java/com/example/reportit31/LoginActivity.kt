package com.example.reportit31

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reportit31.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UIUtils.hideNavigationBar(this)

        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            checkUserRoleAndNavigate(auth.currentUser?.uid ?: "")
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.llSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email"
            binding.etEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.etPassword.requestFocus()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkUserRoleAndNavigate(auth.currentUser?.uid ?: "")
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    val errorMessage = task.exception?.message ?: "Login Failed"
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkUserRoleAndNavigate(uid: String) {
        binding.progressBar.visibility = View.VISIBLE
        database.child("Users").child(uid).child("role").get().addOnSuccessListener { snapshot ->
            binding.progressBar.visibility = View.GONE
            val role = snapshot.value.toString()
            if (role == "admin") {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
            } else {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
            finishAffinity()
        }.addOnFailureListener {
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true
            Toast.makeText(this, "Failed to fetch user role", Toast.LENGTH_SHORT).show()
            // Fallback to user dashboard or logout
            auth.signOut()
        }
    }
}
