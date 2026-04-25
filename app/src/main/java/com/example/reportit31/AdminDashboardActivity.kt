package com.example.reportit31

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.reportit31.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UIUtils.hideNavigationBar(this)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(AdminHomeFragment())
                R.id.nav_complaints -> loadFragment(AdminComplaintsFragment())
                R.id.nav_add -> loadFragment(AdminAddFragment())
                R.id.nav_profile -> loadFragment(AdminProfileFragment())
            }
            true
        }

        // Load Home fragment as default
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun navigateToTab(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }
}

