package com.example.reportit31

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.reportit31.databinding.FragmentAddComplaintBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddComplaintFragment : Fragment() {

    private var _binding: FragmentAddComplaintBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            Toast.makeText(requireContext(), "Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddComplaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val categories = arrayOf("Plumbing", "Electrical", "Cleaning", "Security", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spCategory.adapter = adapter
        binding.spCategory.setSelection(4)

        binding.btnBack.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_home)
        }
        
        binding.btnClose.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_home)
        }

        binding.btnUploadPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        binding.btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun validateAndSubmit() {
        val title = binding.etTitle.text.toString().trim()
        val category = binding.spCategory.selectedItem.toString()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "Title is required"
            return
        }
        if (location.isEmpty()) {
            binding.etLocation.error = "Location is required"
            return
        }

        showLoading(true)
        
        if (selectedImageUri != null) {
            uploadImageToCloudinary(title, category, description, location)
        } else {
            saveComplaintToFirebase(title, category, description, location, "")
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !isLoading
    }

    private fun uploadImageToCloudinary(title: String, category: String, desc: String, loc: String) {
        MediaManager.get().upload(selectedImageUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url").toString()
                    saveComplaintToFirebase(title, category, desc, loc, imageUrl)
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Upload Error: ${error?.description}", Toast.LENGTH_SHORT).show()
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    private fun saveComplaintToFirebase(title: String, category: String, desc: String, loc: String, imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        val complaintId = database.child("Complaints").push().key ?: return
        
        val complaint = mapOf(
            "id" to complaintId,
            "userId" to userId,
            "title" to title,
            "category" to category,
            "description" to desc,
            "location" to loc,
            "imageUrl" to imageUrl,
            "status" to "Pending",
            "timestamp" to System.currentTimeMillis()
        )

        database.child("Complaints").child(complaintId).setValue(complaint)
            .addOnSuccessListener {
                showLoading(false)
                Toast.makeText(requireContext(), "Complaint submitted successfully", Toast.LENGTH_SHORT).show()
                (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_complaints)
            }
            .addOnFailureListener {
                showLoading(false)
                Toast.makeText(requireContext(), "Error saving complaint", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
