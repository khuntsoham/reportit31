package com.example.reportit31

import android.app.Application
import com.cloudinary.android.MediaManager

class ReportItApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "djtbwbfcj",
            "api_key" to "139471922515858",
            "api_secret" to "fAXCF7bZ0SLDD6nelGRfpj3JEUk"
        )
        MediaManager.init(this, config)
    }
}
