package com.example.reportit31

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reportit31.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isEditing = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        
        if (currentUser != null) {
            // Load Real User Data from Firebase
            binding.etEmail.setText(currentUser.email)
            
            database.child("Users").child(currentUser.uid).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    binding.tvUserName.text = name
                    binding.etFullName.setText(name)
                }
            }
        }
        
        binding.tvTotalComplaintsCount.text = "0"
        binding.tvResolvedCount.text = "0"
        
        binding.btnBack.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_home)
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut() // Real Firebase Sign Out
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finishAffinity()
        }

        binding.btnEditProfile.setOnClickListener {
            if (!isEditing) {
                isEditing = true
                binding.btnEditProfile.text = "Save Profile"
                
                binding.etFullName.isEnabled = true
                binding.etEmail.isEnabled = false // Email shouldn't be edited normally
                binding.etPhone.isEnabled = true
                binding.etFlatDetails.isEnabled = true
                
                binding.etFullName.requestFocus()
            } else {
                saveProfile()
            }
        }
    }

    private fun saveProfile() {
        val newName = binding.etFullName.text.toString().trim()
        val userId = auth.currentUser?.uid
        
        if (newName.isEmpty()) {
            binding.etFullName.error = "Name cannot be empty"
            return
        }

        if (userId != null) {
            database.child("Users").child(userId).child("name").setValue(newName)
                .addOnSuccessListener {
                    isEditing = false
                    binding.btnEditProfile.text = "Edit Profile"
                    binding.etFullName.isEnabled = false
                    binding.etPhone.isEnabled = false
                    binding.etFlatDetails.isEnabled = false
                    binding.tvUserName.text = newName
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
