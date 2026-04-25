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
            binding.etEmail.setText(currentUser.email)
            
            database.child("Users").child(currentUser.uid).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: ""
                    val phone = snapshot.child("phone").value?.toString() ?: ""
                    val wing = snapshot.child("wing").value?.toString() ?: ""
                    val flat = snapshot.child("flat").value?.toString() ?: ""
                    
                    val flatDetails = if (wing.isNotEmpty() && flat.isNotEmpty()) "$wing - $flat" else "$wing$flat"
                    
                    binding.tvUserName.text = name
                    binding.etFullName.setText(name)
                    binding.etPhone.setText(phone)
                    binding.etFlatDetails.setText(flatDetails)
                }
            }

            // Fetch Complaint Counts for this user
            database.child("Complaints").orderByChild("userId").equalTo(currentUser.uid)
                .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                    override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                        var total = 0
                        var resolved = 0
                        for (child in snapshot.children) {
                            total++
                            val status = child.child("status").getValue(String::class.java)
                            if (status != null && status.equals("resolved", ignoreCase = true)) {
                                resolved++
                            }
                        }
                        binding.tvTotalComplaintsCount.text = total.toString()
                        binding.tvResolvedCount.text = resolved.toString()
                    }
                    override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
                })
        }
        
        binding.btnBack.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_home)
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
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
                binding.etFlatDetails.isEnabled = false // Let's keep complex split logic safe or only let them edit it if they do it right
                
                binding.etFullName.requestFocus()
            } else {
                saveProfile()
            }
        }
    }

    private fun saveProfile() {
        val newName = binding.etFullName.text.toString().trim()
        val newPhone = binding.etPhone.text.toString().trim()
        val userId = auth.currentUser?.uid
        
        if (newName.isEmpty()) {
            binding.etFullName.error = "Name cannot be empty"
            return
        }

        if (userId != null) {
            val updates = mapOf(
                "name" to newName,
                "phone" to newPhone
            )
            database.child("Users").child(userId).updateChildren(updates)
                .addOnSuccessListener {
                    isEditing = false
                    binding.btnEditProfile.text = "Edit Profile"
                    binding.etFullName.isEnabled = false
                    binding.etPhone.isEnabled = false
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
