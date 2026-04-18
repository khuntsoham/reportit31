package com.example.reportit31

data class Complaint(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val status: String = "Pending",
    val timestamp: Long = 0L
) {
    // UI Helpers
    fun getStatusColor(): String {
        return when (status.lowercase()) {
            "pending" -> "#F59E0B" // Amber
            "in progress", "active" -> "#3B82F6" // Blue
            "resolved", "complete" -> "#10B981" // Green
            else -> "#6B7280" // Grey
        }
    }

    fun getStatusBg(): Int {
        return when (status.lowercase()) {
            "pending" -> R.drawable.bg_pill_light_yellow
            "in progress", "active" -> R.drawable.bg_pill_light_blue
            "resolved", "complete" -> R.drawable.bg_pill_light_green
            else -> R.drawable.bg_pill_grey
        }
    }
}
