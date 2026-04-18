package com.example.reportit31

object DataRepository {
    
    val allComplaints = mutableListOf<Complaint>()
    
    init {
        allComplaints.add(Complaint(
            "1",
            "mock_user_id",
            "Water Leakage in Bathroom",
            "Plumbing",
            "Severe water leakage from the ceiling in the master bathroom. Water dripping...",
            "Tower A, Floor 5, Flat 502",
            "",
            "In Progress",
            System.currentTimeMillis() - 86400000 // 1 day ago
        ))
        allComplaints.add(Complaint(
            "2",
            "mock_user_id",
            "Lift Not Working",
            "Maintenance",
            "Lift in Tower B is out of service since morning. Senior citizens are facing difficult...",
            "Tower B, Lift 2",
            "",
            "Pending",
            System.currentTimeMillis() - 172800000 // 2 days ago
        ))
        allComplaints.add(Complaint(
            "3",
            "mock_user_id",
            "Security Gate Issue",
            "Security",
            "Main entrance gate automatic opening mechanism is faulty. Causing inconvenienc...",
            "Main Gate Entrance",
            "",
            "Resolved",
            System.currentTimeMillis() - 259200000 // 3 days ago
        ))
        allComplaints.add(Complaint(
            "4",
            "mock_user_id",
            "Gym Equipment Broken",
            "Amenities",
            "Treadmill in the community gym is not working properly. Display screen is blank.",
            "Community Gym, Ground Floor",
            "",
            "Pending",
            System.currentTimeMillis() - 345600000 // 4 days ago
        ))
    }

    fun getRecentComplaints(): List<Complaint> {
        return allComplaints.take(2)
    }
    
    fun getPendingCount(): Int {
        return allComplaints.count { it.status.equals("pending", ignoreCase = true) }
    }
    
    fun getActiveCount(): Int {
        return allComplaints.count { it.status.equals("In Progress", ignoreCase = true) || it.status.equals("Active", ignoreCase = true) }
    }
    
    fun getResolvedCount(): Int {
        return allComplaints.count { it.status.equals("resolved", ignoreCase = true) || it.status.equals("Resolved", ignoreCase = true) }
    }
    
    fun getTotalCount(): Int {
        return allComplaints.size
    }
}
