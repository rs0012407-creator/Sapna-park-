package com.example.data.repository

import com.example.data.local.SapanaParkDao
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

class SapanaParkRepository(private val dao: SapanaParkDao) {

    val residentProfile: Flow<ResidentProfile?> = dao.getResidentProfile()
    val allBills: Flow<List<MaintenanceBill>> = dao.getAllBills()
    val allNotices: Flow<List<Notice>> = dao.getAllNotices()
    val allComplaints: Flow<List<Complaint>> = dao.getAllComplaints()
    val allEvents: Flow<List<CommunityEvent>> = dao.getAllEvents()
    val allDocuments: Flow<List<SocietyDocument>> = dao.getAllDocuments()
    val allNocRequests: Flow<List<NocRequest>> = dao.getAllNocRequests()
    val allUtilityBills: Flow<List<UtilityBill>> = dao.getAllUtilityBills()
    val savedConsumerNumbers: Flow<List<SavedConsumerNumber>> = dao.getAllSavedConsumerNumbers()

    fun getVehicles(flatNo: String): Flow<List<Vehicle>> = dao.getVehiclesForFlat(flatNo)

    suspend fun updateProfile(profile: ResidentProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun payBill(billId: Long, paymentDate: String, txnRef: String) {
        dao.markBillPaid(billId, paymentDate, txnRef)
    }

    suspend fun createComplaint(complaint: Complaint) {
        dao.insertComplaint(complaint)
    }

    suspend fun deleteComplaint(id: Long) {
        dao.deleteComplaintById(id)
    }

    suspend fun updateComplaintStatus(id: Long, status: ComplaintStatus, timelineJson: String) {
        dao.updateComplaintStatus(id, status, timelineJson)
    }

    suspend fun saveConsumerNumber(saved: SavedConsumerNumber) {
        dao.saveConsumerNumber(saved)
    }

    suspend fun insertUtilityBill(bill: UtilityBill) {
        dao.insertUtilityBill(bill)
    }

    suspend fun payUtilityBill(id: Long, date: String, ref: String, method: String, discount: Double) {
        dao.markUtilityBillPaid(id, date, ref, method, discount)
    }

    suspend fun submitNocRequest(request: NocRequest) {
        dao.insertNocRequest(request)
    }

    suspend fun addVehicle(vehicle: Vehicle) {
        dao.insertVehicle(vehicle)
    }

    suspend fun postNotice(notice: Notice) {
        dao.insertNotice(notice)
    }

    suspend fun createEvent(event: CommunityEvent) {
        dao.insertEvent(event)
    }

    suspend fun deleteEvent(eventId: Long) {
        dao.deleteEventById(eventId)
    }
}
