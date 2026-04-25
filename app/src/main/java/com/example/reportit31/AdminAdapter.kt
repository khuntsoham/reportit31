package com.example.reportit31

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reportit31.databinding.ItemAdminComplaintBinding
import com.example.reportit31.databinding.ItemAdminUserBinding
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminAdapter(
    private val onComplaintClick: (Complaint) -> Unit,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = mutableListOf<Any>()
    private val TYPE_COMPLAINT = 0
    private val TYPE_USER = 1
    private val database = FirebaseDatabase.getInstance().reference

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is Complaint) TYPE_COMPLAINT else TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_COMPLAINT) {
            ComplaintViewHolder(ItemAdminComplaintBinding.inflate(inflater, parent, false))
        } else {
            UserViewHolder(ItemAdminUserBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        if (holder is ComplaintViewHolder && item is Complaint) {
            holder.bind(item)
        } else if (holder is UserViewHolder && item is User) {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setComplaints(list: List<Complaint>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun setUsers(list: List<User>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class ComplaintViewHolder(private val binding: ItemAdminComplaintBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(complaint: Complaint) {
            binding.tvTitle.text = complaint.title
            binding.tvLocation.text = complaint.location
            binding.tvDesc.text = complaint.description
            binding.tvStatus.text = complaint.status
            
            val sdf = SimpleDateFormat("dd MMM, yyyy HH:mm", Locale.getDefault())
            binding.tvDate.text = sdf.format(Date(complaint.timestamp))
            
            val colorStr = complaint.getStatusColor()
            binding.tvStatus.setBackgroundResource(complaint.getStatusBg())
            binding.tvStatus.setTextColor(Color.parseColor(colorStr))

            // Fetch missing User specifics (Name + Flat) dynamically linked
            database.child("Users").child(complaint.userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value?.toString() ?: "Unknown"
                    val wing = snapshot.child("wing").value?.toString() ?: ""
                    val flat = snapshot.child("flat").value?.toString() ?: ""
                    binding.tvUserInfo.text = "$name ($wing-$flat)"
                } else {
                    binding.tvUserInfo.text = "Unknown Resident"
                }
            }

            binding.root.setOnClickListener { onComplaintClick(complaint) }
        }
    }

    inner class UserViewHolder(private val binding: ItemAdminUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email
            binding.tvUserRole.text = user.role.uppercase()
            
            binding.root.setOnClickListener { onUserClick(user) }
        }
    }
}
