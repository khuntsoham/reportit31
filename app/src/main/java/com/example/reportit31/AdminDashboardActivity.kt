package com.example.reportit31

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reportit31.databinding.ActivityAdminDashboardBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference
    
    private val allComplaints = mutableListOf<Complaint>()
    private val allUsers = mutableListOf<User>()
    private lateinit var adminAdapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UIUtils.hideNavigationBar(this)

        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        setupTabs()

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        fetchComplaints()
    }

    private fun setupRecyclerView() {
        adminAdapter = AdminAdapter(
            onComplaintClick = { complaint -> showStatusUpdateDialog(complaint) },
            onUserClick = { user -> /* Show user details if needed */ }
        )
        binding.rvAdmin.layoutManager = LinearLayoutManager(this)
        binding.rvAdmin.adapter = adminAdapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> fetchComplaints()
                    1 -> fetchUsers()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchComplaints() {
        binding.progressBar.visibility = View.VISIBLE
        database.child("Complaints").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allComplaints.clear()
                for (postSnapshot in snapshot.children) {
                    val complaint = postSnapshot.getValue(Complaint::class.java)
                    complaint?.let { allComplaints.add(it) }
                }
                if (binding.tabLayout.selectedTabPosition == 0) {
                    adminAdapter.setComplaints(allComplaints)
                }
                binding.progressBar.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun fetchUsers() {
        binding.progressBar.visibility = View.VISIBLE
        database.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allUsers.clear()
                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    user?.let { allUsers.add(it) }
                }
                if (binding.tabLayout.selectedTabPosition == 1) {
                    adminAdapter.setUsers(allUsers)
                }
                binding.progressBar.visibility = View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun showStatusUpdateDialog(complaint: Complaint) {
        val statuses = arrayOf("Pending", "In Progress", "Resolved")
        AlertDialog.Builder(this)
            .setTitle("Update Status")
            .setItems(statuses) { _, which ->
                val newStatus = statuses[which]
                updateComplaintStatus(complaint.id, newStatus)
            }
            .show()
    }

    private fun updateComplaintStatus(complaintId: String, newStatus: String) {
        database.child("Complaints").child(complaintId).child("status").setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
            }
    }
}

