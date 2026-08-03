package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ResidentType {
    OWNER,
    TENANT
}

enum class BillStatus {
    UNPAID,
    PAID,
    OVERDUE
}

enum class ComplaintStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}

enum class ComplaintCategory {
    PLUMBING,
    ELECTRICAL,
    ELEVATOR,
    PARKING,
    SECURITY,
    SANITATION,
    GENERAL
}

enum class DocumentCategory {
    BYE_LAWS,
    NOC_FORM,
    GOA_SOCIETY_ACT,
    MEETING_MINUTES,
    AUDIT_REPORT
}

@Entity(tableName = "resident_profile")
data class ResidentProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val flatNo: String, // e.g. Block A-204
    val phone: String,
    val email: String,
    val residentType: ResidentType,
    val moveInDate: String,
    val vehicleCount: Int,
    val isVerified: Boolean = true,
    val avatarUri: String? = null,
    val referralCode: String = "SAPANA50",
    val referralBonusWallet: Double = 50.0,
    val notificationsEnabled: Boolean = true,
    val roomNumber: String = "",
    val floorNumber: String = "",
    val registrationDate: String = "",
    val smsPromotionalAllowed: Boolean = true,
    val locationPromotionalAllowed: Boolean = true
)

@Entity(tableName = "tenants")
data class Tenant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val flatNo: String,
    val tenantName: String,
    val contactPhone: String,
    val agreementExpiry: String,
    val policeVerificationStatus: String // Verified, Pending, Submitted
)

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val registrationNo: String, // e.g. GA-03-AB-1234
    val flatNo: String,
    val vehicleType: String, // Car / Two Wheeler
    val parkingSlot: String
)

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // General, Urgent, Maintenance, Meeting, Festival
    val content: String,
    val date: String,
    val priority: String, // HIGH, NORMAL
    val author: String
)

@Entity(tableName = "maintenance_bills")
data class MaintenanceBill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val billNumber: String,
    val flatNo: String,
    val monthYear: String, // e.g. August 2026
    val dueDate: String,
    val baseMaintenance: Double,
    val waterCharges: Double,
    val sinkingFund: Double,
    val lateFee: Double = 0.0,
    val totalAmount: Double,
    val status: BillStatus,
    val paymentDate: String? = null,
    val transactionRef: String? = null
)

@Entity(tableName = "complaints")
data class Complaint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketNo: String, // e.g. SP-2026-102
    val flatNo: String,
    val residentName: String,
    val category: ComplaintCategory,
    val description: String,
    val status: ComplaintStatus,
    val createdDate: String,
    val expectedResolutionDate: String,
    val assignedStaff: String,
    val timelineLogsJson: String, // Serialized list of timeline entries
    val photoUri: String? = null,
    val isUserLogged: Boolean = true
)

enum class UtilityCategory {
    ELECTRICITY,
    WATER,
    HOME_RENT,
    MAINTENANCE,
    OTHER
}

@Entity(tableName = "utility_bills")
data class UtilityBill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: UtilityCategory,
    val title: String,
    val consumerNumber: String,
    val billerName: String,
    val amount: Double,
    val dueDate: String,
    val isPaid: Boolean = false,
    val paymentDate: String? = null,
    val transactionRef: String? = null,
    val paymentMethod: String? = null, // UPI_APP, UPI_QR, NET_BANKING, CARD
    val discountApplied: Double = 0.0
)

@Entity(tableName = "saved_consumer_numbers")
data class SavedConsumerNumber(
    @PrimaryKey val categoryKey: String, // e.g. ELECTRICITY, WATER, HOME_RENT
    val consumerNumber: String,
    val billerName: String,
    val defaultAmount: Double
)

data class ComplaintTimelineLog(
    val timestamp: String,
    val statusText: String,
    val note: String
)

@Entity(tableName = "community_events")
data class CommunityEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String,
    val time: String,
    val venue: String,
    val description: String,
    val organizer: String,
    val rsvpCount: Int = 12,
    val isEmpowermentProgram: Boolean = false,
    val empowermentCategory: String? = null, // Women's Skill Workshop, Eco-Green Initiative, Senior Citizen Club
    val posterUrl: String? = null,
    val isCompleted: Boolean = false,
    val eventLocationAddress: String = "Sapana Park Clubhouse Ground Floor, Porvorim, North Goa",
    val locationGeoUri: String = "geo:15.5262,73.8315?q=Sapana+Park+CHS+Porvorim"
)

@Entity(tableName = "society_documents")
data class SocietyDocument(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: DocumentCategory,
    val description: String,
    val fileSize: String,
    val dateAdded: String,
    val referenceAct: String? = null // e.g. "Section 73, Goa Cooperative Societies Act, 2001"
)

@Entity(tableName = "noc_requests")
data class NocRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestRefNo: String,
    val flatNo: String,
    val residentName: String,
    val nocType: String, // Sale NOC, Tenant Verification NOC, Renovation NOC, Electricity Meter Transfer
    val reason: String,
    val requestedDate: String,
    val status: String // PENDING_APPROVAL, APPROVED, REJECTED
)
