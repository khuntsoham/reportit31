package com.example.reportit31

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reportit31.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPendingCount.text = DataRepository.getPendingCount().toString()
        binding.tvActiveCount.text = DataRepository.getActiveCount().toString()
        binding.tvCompletedCount.text = DataRepository.getResolvedCount().toString()

        binding.rvComplaints.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.rvComplaints.adapter = ComplaintAdapter(DataRepository.getRecentComplaints())

        binding.cardActionReport.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_add)
        }
        
        binding.cardActionView.setOnClickListener {
            (requireActivity() as? DashboardActivity)?.navigateToTab(R.id.nav_complaints)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
