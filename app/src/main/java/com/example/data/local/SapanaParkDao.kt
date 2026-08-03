package com.example.data.local

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SapanaParkDao {

    // Resident Profile
    @Query("SELECT * FROM resident_profile WHERE id = 1")
    fun getResidentProfile(): Flow<ResidentProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ResidentProfile)

    // Maintenance Bills
    @Query("SELECT * FROM maintenance_bills ORDER BY id DESC")
    fun getAllBills(): Flow<List<MaintenanceBill>>

    @Query("SELECT * FROM maintenance_bills WHERE status = :status ORDER BY id DESC")
    fun getBillsByStatus(status: BillStatus): Flow<List<MaintenanceBill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: MaintenanceBill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<MaintenanceBill>)

    @Query("UPDATE maintenance_bills SET status = 'PAID', paymentDate = :paymentDate, transactionRef = :txnRef WHERE id = :billId")
    suspend fun markBillPaid(billId: Long, paymentDate: String, txnRef: String)

    // Notices
    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<Notice>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice)

    // Complaints
    @Query("SELECT * FROM complaints ORDER BY id DESC")
    fun getAllComplaints(): Flow<List<Complaint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: Complaint)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaints(complaints: List<Complaint>)

    @Query("UPDATE complaints SET status = :status, timelineLogsJson = :timelineJson WHERE id = :id")
    suspend fun updateComplaintStatus(id: Long, status: ComplaintStatus, timelineJson: String)

    @Query("DELETE FROM complaints WHERE id = :id")
    suspend fun deleteComplaintById(id: Long)

    // Utility Bills
    @Query("SELECT * FROM utility_bills ORDER BY id DESC")
    fun getAllUtilityBills(): Flow<List<UtilityBill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUtilityBill(bill: UtilityBill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUtilityBills(bills: List<UtilityBill>)

    @Query("UPDATE utility_bills SET isPaid = 1, paymentDate = :date, transactionRef = :ref, paymentMethod = :method, discountApplied = :discount WHERE id = :id")
    suspend fun markUtilityBillPaid(id: Long, date: String, ref: String, method: String, discount: Double)

    // Saved Consumer Numbers
    @Query("SELECT * FROM saved_consumer_numbers")
    fun getAllSavedConsumerNumbers(): Flow<List<SavedConsumerNumber>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConsumerNumber(savedConsumerNumber: SavedConsumerNumber)

    // Events
    @Query("SELECT * FROM community_events ORDER BY id DESC")
    fun getAllEvents(): Flow<List<CommunityEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<CommunityEvent>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CommunityEvent)

    @Query("DELETE FROM community_events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Long)

    @Delete
    suspend fun deleteEvent(event: CommunityEvent)

    // Society Documents
    @Query("SELECT * FROM society_documents ORDER BY id ASC")
    fun getAllDocuments(): Flow<List<SocietyDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<SocietyDocument>)

    // NOC Requests
    @Query("SELECT * FROM noc_requests ORDER BY id DESC")
    fun getAllNocRequests(): Flow<List<NocRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNocRequest(request: NocRequest)

    // Vehicles
    @Query("SELECT * FROM vehicles WHERE flatNo = :flatNo")
    fun getVehiclesForFlat(flatNo: String): Flow<List<Vehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<Vehicle>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle)
}
