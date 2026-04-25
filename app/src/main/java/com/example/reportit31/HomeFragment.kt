package com.example.reportit31

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reportit31.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val recentList = mutableListOf<Complaint>()
    private lateinit var adapter: ComplaintAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        
        setupRecyclerView()
        fetchUserData()
        fetchUserComplaints()

        binding.cardActionReport.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_add)
        }
        
        binding.cardActionView.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_complaints)
        }
    }
    
    private fun setupRecyclerView() {
        adapter = ComplaintAdapter(recentList)
        binding.rvComplaints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComplaints.adapter = adapter
    }
    
    private fun fetchUserData() {
        val uid = auth.currentUser?.uid ?: return
        database.child("Users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                if (name != null) {
                    binding.tvLogoText.text = "Welcome, $name"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    
    private fun fetchUserComplaints() {
        val uid = auth.currentUser?.uid ?: return
        database.child("Complaints").orderByChild("userId").equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    recentList.clear()
                    var pending = 0
                    var active = 0
                    var resolved = 0
                    
                    for (postSnapshot in snapshot.children) {
                        val complaint = postSnapshot.getValue(Complaint::class.java)
                        if (complaint != null) {
                            recentList.add(complaint)
                            when (complaint.status.lowercase()) {
                                "pending" -> pending++
                                "in progress", "active" -> active++
                                "resolved" -> resolved++
                            }
                        }
                    }
                    
                    binding.tvPendingCount.text = pending.toString()
                    binding.tvActiveCount.text = active.toString()
                    binding.tvCompletedCount.text = resolved.toString()
                    
                    // Sort descending by date and grab first two
                    recentList.sortByDescending { it.timestamp }
                    val displayList = recentList.take(2)
                    adapter.updateData(displayList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load complaints", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
