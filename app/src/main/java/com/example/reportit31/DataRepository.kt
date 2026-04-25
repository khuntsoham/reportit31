package com.example.reportit31

object DataRepository {
    
    private val complaintsList = mutableListOf<Complaint>()
    
    init {
        complaintsList.add(Complaint(
            id = "1",
            userId = "mock_user_id",
            title = "Water Leakage in Bathroom",
            category = "Plumbing",
            description = "Severe water leakage from the ceiling in the master bathroom. Water dripping...",
            location = "Tower A, Floor 5, Flat 502",
            imageUrl = "",
            status = "In Progress",
            timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
        ))
        complaintsList.add(Complaint(
            id = "2",
            userId = "mock_user_id",
            title = "Lift Not Working",
            category = "Maintenance",
            description = "Lift in Tower B is out of service since morning. Senior citizens are facing difficult...",
            location = "Tower B, Lift 2",
            imageUrl = "",
            status = "Pending",
            timestamp = System.currentTimeMillis() - 172800000 // 2 days ago
        ))
        complaintsList.add(Complaint(
            id = "3",
            userId = "mock_user_id",
            title = "Security Gate Issue",
            category = "Security",
            description = "Main entrance gate automatic opening mechanism is faulty. Causing inconvenienc...",
            location = "Main Gate Entrance",
            imageUrl = "",
            status = "Resolved",
            timestamp = System.currentTimeMillis() - 259200000 // 3 days ago
        ))
        complaintsList.add(Complaint(
            id = "4",
            userId = "mock_user_id",
            title = "Gym Equipment Broken",
            category = "Amenities",
            description = "Treadmill in the community gym is not working properly. Display screen is blank.",
            location = "Community Gym, Ground Floor",
            imageUrl = "",
            status = "Pending",
            timestamp = System.currentTimeMillis() - 345600000 // 4 days ago
        ))
    }

    fun getRecentComplaints(): List<Complaint> {
        return complaintsList.sortedByDescending { it.timestamp }.take(2)
    }
    
    fun getPendingCount(): Int {
        return complaintsList.count { it.status.equals("pending", ignoreCase = true) }
    }
    
    fun getActiveCount(): Int {
        return complaintsList.count { it.status.equals("In Progress", ignoreCase = true) || it.status.equals("Active", ignoreCase = true) }
    }
    
    fun getResolvedCount(): Int {
        return complaintsList.count { it.status.equals("resolved", ignoreCase = true) || it.status.equals("Resolved", ignoreCase = true) }
    }
    
    fun getTotalCount(): Int {
        return complaintsList.size
    }

    fun getAllComplaints(): List<Complaint> {
        return complaintsList.sortedByDescending { it.timestamp }
    }

    fun updateComplaintStatus(complaintId: String, newStatus: String) {
        val index = complaintsList.indexOfFirst { it.id == complaintId }
        if (index != -1) {
            val old = complaintsList[index]
            complaintsList[index] = old.copy(status = newStatus)
        }
    }
}
