package com.example.reportit31

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reportit31.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var isPasswordVisible = false
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UIUtils.hideNavigationBar(this)

        auth = FirebaseAuth.getInstance()

        binding.ivBack.setOnClickListener { finish() }

        binding.ivTogglePassword.setOnClickListener { togglePasswordVisibility() }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLogin.setOnClickListener { finish() }
        binding.llLogin.setOnClickListener { finish() }
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val termsChecked = binding.cbTerms.isChecked

        // Validations
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return
        }
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return
        }
        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return
        }
        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return
        }
        if (!termsChecked) {
            Toast.makeText(this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userMap = mapOf(
                        "uid" to userId,
                        "name" to name,
                        "email" to email,
                        "role" to "user",
                        "createdAt" to System.currentTimeMillis()
                    )

                    // Store user data in Realtime Database
                    if (userId != null) {
                        database.child("Users").child(userId).setValue(userMap)
                            .addOnSuccessListener {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this, "Registration Successful! Please Login.", Toast.LENGTH_LONG).show()
                                
                                // Sign out the user immediately after registration 
                                // to force them to go through the Login screen.
                                auth.signOut()
                                
                                // Navigate to Login Screen
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                binding.progressBar.visibility = View.GONE
                                binding.btnRegister.isEnabled = true
                                Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        val transformation = if (isPasswordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        binding.etPassword.transformationMethod = transformation
        binding.etConfirmPassword.transformationMethod = transformation
        binding.etPassword.setSelection(binding.etPassword.text.length)
        binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text.length)
    }
}
