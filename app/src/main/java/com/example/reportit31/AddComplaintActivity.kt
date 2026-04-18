package com.example.reportit31

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reportit31.databinding.ActivityAddComplaintBinding

class AddComplaintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddComplaintBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup Category Spinner
        val categories = arrayOf("Plumbing", "Electrical", "Cleaning", "Security", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spCategory.adapter = adapter
        
        // Set default to "Other" (index 4)
        binding.spCategory.setSelection(4)

        // Header actions
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnClose.setOnClickListener {
            finish()
        }

        // Photo Upload placeholder click
        binding.btnUploadPhoto.setOnClickListener {
            Toast.makeText(this, "Photo upload coming soon", Toast.LENGTH_SHORT).show()
        }

        // Submit Button logic
        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val category = binding.spCategory.selectedItem.toString()
            val description = binding.etDescription.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Title is required"
                return@setOnClickListener
            }
            
            if (location.isEmpty()) {
                binding.etLocation.error = "Location is required"
                return@setOnClickListener
            }

            // TODO: Integrate with Backend/Firebase
            Toast.makeText(this, "Complaint for $category submitted successfully", Toast.LENGTH_LONG).show()
            finish()
        }
        
        // Bottom Navigation setup
        binding.bottomNavigation.selectedItemId = R.id.nav_add
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate to Dashboard
                    finish()
                    true
                }
                R.id.nav_complaints -> {
                    // Already in a complaint related flow or navigate to list
                    true
                }
                R.id.nav_add -> true
                R.id.nav_profile -> {
                    // Navigate to Profile
                    true
                }
                else -> false
            }
        }
    }
}
