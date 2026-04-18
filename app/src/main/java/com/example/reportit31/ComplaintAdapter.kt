package com.example.reportit31

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reportit31.databinding.ItemMyComplaintBinding
import java.text.SimpleDateFormat
import java.util.*

class ComplaintAdapter(private var complaintsList: List<Complaint>) :
    RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    inner class ComplaintViewHolder(val binding: ItemMyComplaintBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val binding = ItemMyComplaintBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ComplaintViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaintsList[position]
        
        holder.binding.tvTitle.text = complaint.title
        holder.binding.tvDesc.text = complaint.description
        holder.binding.tvLocation.text = complaint.location
        holder.binding.tvCategory.text = complaint.category
        
        // Format Timestamp to Date
        val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        holder.binding.tvDate.text = sdf.format(Date(complaint.timestamp))
        
        holder.binding.tvStatusPill.text = complaint.status

        // Status Pill Styling from helpers
        val colorStr = complaint.getStatusColor()
        holder.binding.tvStatusPill.setBackgroundResource(complaint.getStatusBg())
        holder.binding.tvStatusPill.setTextColor(Color.parseColor(colorStr))
        holder.binding.statusDot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorStr))
    }

    override fun getItemCount(): Int = complaintsList.size

    fun updateData(newList: List<Complaint>) {
        complaintsList = newList
        notifyDataSetChanged()
    }
}
