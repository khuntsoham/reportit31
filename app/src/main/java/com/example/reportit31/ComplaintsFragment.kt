package com.example.reportit31

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reportit31.databinding.FragmentComplaintsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ComplaintsFragment : Fragment() {

    private var _binding: FragmentComplaintsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: ComplaintAdapter
    private val allComplaints = mutableListOf<Complaint>()
    private var currentStatusFilter: String? = null
    private var currentSearchQuery: String = ""
    
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComplaintsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnBack.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_home)
        }
        
        setupRecyclerView()
        setupFilters()
        setupSearch()
        fetchComplaintsFromFirebase()
    }
    
    private fun setupRecyclerView() {
        adapter = ComplaintAdapter(mutableListOf())
        binding.rvComplaints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComplaints.adapter = adapter
    }
    
    private fun fetchComplaintsFromFirebase() {
        val userId = auth.currentUser?.uid ?: return
        
        binding.progressBar.visibility = View.VISIBLE
        
        // Listen for user-specific complaints
        database.child("Complaints").orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded) return
                    binding.progressBar.visibility = View.GONE
                    
                    allComplaints.clear()
                    for (complaintSnapshot in snapshot.children) {
                        val complaint = complaintSnapshot.getValue(Complaint::class.java)
                        if (complaint != null) {
                            allComplaints.add(complaint)
                        }
                    }
                    
                    // Sort by newest first
                    allComplaints.sortByDescending { it.timestamp }
                    
                    filterComplaints()
                }

                override fun onCancelled(error: DatabaseError) {
                    if (!isAdded) return
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    
    private fun setupFilters() {
        val filters = listOf(
            binding.btnFilterAll,
            binding.btnFilterPending,
            binding.btnFilterActive,
            binding.btnFilterResolved
        )
        
        val selectFilter = { selectedBtn: TextView, statusFilter: String? ->
            filters.forEach { btn ->
                btn.setBackgroundResource(R.drawable.bg_pill_grey)
                btn.setTextColor(Color.parseColor("#64748B"))
                btn.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
            
            selectedBtn.setBackgroundResource(R.drawable.bg_pill_blue)
            selectedBtn.setTextColor(Color.WHITE)
            selectedBtn.setTypeface(null, android.graphics.Typeface.BOLD)
            
            currentStatusFilter = statusFilter
            filterComplaints()
        }
        
        binding.btnFilterAll.setOnClickListener { selectFilter(binding.btnFilterAll, null) }
        binding.btnFilterPending.setOnClickListener { selectFilter(binding.btnFilterPending, "Pending") }
        binding.btnFilterActive.setOnClickListener { selectFilter(binding.btnFilterActive, "Active") }
        binding.btnFilterResolved.setOnClickListener { selectFilter(binding.btnFilterResolved, "Resolved") }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                currentSearchQuery = s?.toString() ?: ""
                filterComplaints()
            }
        })
    }
    
    private fun filterComplaints() {
        val filteredByStatus = if (currentStatusFilter == null) {
            allComplaints
        } else {
            allComplaints.filter { it.status.equals(currentStatusFilter, ignoreCase = true) }
        }
        
        val finalFiltered = if (currentSearchQuery.isEmpty()) {
            filteredByStatus
        } else {
            filteredByStatus.filter {
                it.title.contains(currentSearchQuery, ignoreCase = true) || 
                it.description.contains(currentSearchQuery, ignoreCase = true) ||
                it.category.contains(currentSearchQuery, ignoreCase = true) ||
                it.location.contains(currentSearchQuery, ignoreCase = true)
            }
        }
        
        adapter.updateData(finalFiltered)
        
        // Show empty state if no complaints
        if (finalFiltered.isEmpty()) {
            binding.rvComplaints.visibility = View.GONE
            // Optional: binding.tvEmptyMessage.visibility = View.VISIBLE
        } else {
            binding.rvComplaints.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
