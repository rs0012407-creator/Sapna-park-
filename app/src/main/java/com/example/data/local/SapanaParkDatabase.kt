package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ResidentProfile::class,
        Tenant::class,
        Vehicle::class,
        Notice::class,
        MaintenanceBill::class,
        Complaint::class,
        CommunityEvent::class,
        SocietyDocument::class,
        NocRequest::class,
        UtilityBill::class,
        SavedConsumerNumber::class
    ],
    version = 3,
    exportSchema = false
)
abstract class SapanaParkDatabase : RoomDatabase() {

    abstract fun sapanaParkDao(): SapanaParkDao

    companion object {
        @Volatile
        private var INSTANCE: SapanaParkDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SapanaParkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SapanaParkDatabase::class.java,
                    "sapana_park_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(SapanaParkDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SapanaParkDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.sapanaParkDao())
                }
            }
        }

        suspend fun populateInitialData(dao: SapanaParkDao) {
            // Default Profile
            dao.insertOrUpdateProfile(
                ResidentProfile(
                    id = 1,
                    name = "Rajesh Chodankar",
                    flatNo = "Block A-204",
                    phone = "+91 98221 45678",
                    email = "rajesh.chodankar@sapanapark.org",
                    residentType = ResidentType.OWNER,
                    moveInDate = "15 Jan 2019",
                    vehicleCount = 2,
                    roomNumber = "204",
                    floorNumber = "2nd Floor",
                    registrationDate = "15 Jan 2019"
                )
            )

            // Initial Maintenance Bills
            dao.insertBills(
                listOf(
                    MaintenanceBill(
                        billNumber = "SP-AUG-2026-0204",
                        flatNo = "Block A-204",
                        monthYear = "August 2026",
                        dueDate = "15 Aug 2026",
                        baseMaintenance = 2200.0,
                        waterCharges = 350.0,
                        sinkingFund = 300.0,
                        lateFee = 0.0,
                        totalAmount = 2850.0,
                        status = BillStatus.UNPAID
                    ),
                    MaintenanceBill(
                        billNumber = "SP-JUL-2026-0204",
                        flatNo = "Block A-204",
                        monthYear = "July 2026",
                        dueDate = "15 Jul 2026",
                        baseMaintenance = 2200.0,
                        waterCharges = 320.0,
                        sinkingFund = 300.0,
                        lateFee = 0.0,
                        totalAmount = 2820.0,
                        status = BillStatus.PAID,
                        paymentDate = "12 Jul 2026",
                        transactionRef = "UPI/628912/PAYTM"
                    ),
                    MaintenanceBill(
                        billNumber = "SP-JUN-2026-0204",
                        flatNo = "Block A-204",
                        monthYear = "June 2026",
                        dueDate = "15 Jun 2026",
                        baseMaintenance = 2200.0,
                        waterCharges = 380.0,
                        sinkingFund = 300.0,
                        lateFee = 0.0,
                        totalAmount = 2880.0,
                        status = BillStatus.PAID,
                        paymentDate = "10 Jun 2026",
                        transactionRef = "UPI/519203/HDFC"
                    )
                )
            )

            // Initial Notices
            dao.insertNotices(
                listOf(
                    Notice(
                        title = "Monsoon Terrace Drainage Cleaning & Tank Inspection",
                        category = "Maintenance",
                        content = "Please note that terrace water drainage lines across Block A & Block B will undergo quarterly inspection on Saturday, 8th August 2026 between 9:00 AM - 1:00 PM. Water supply may be interrupted briefly.",
                        date = "01 Aug 2026",
                        priority = "HIGH",
                        author = "Management Committee"
                    ),
                    Notice(
                        title = "Annual General Body Meeting (AGM) Notice 2026",
                        category = "Meeting",
                        content = "The Annual General Body Meeting of Sapana Park CHS Ltd will be convened on Sunday, 23rd August 2026 at 10:30 AM in the Society Clubhouse. Agenda includes financial audit approval and green solar rooftop proposal.",
                        date = "28 Jul 2026",
                        priority = "HIGH",
                        author = "Secretary - Mr. Anand Naik"
                    ),
                    Notice(
                        title = "Goa Liberation & Cultural Night Celebration",
                        category = "Festival",
                        content = "Cultural committee invites all residents for traditional Goan music, food stalls, and children's talent competition at the central courtyard.",
                        date = "25 Jul 2026",
                        priority = "NORMAL",
                        author = "Cultural Club"
                    )
                )
            )

            // Initial Complaints
            dao.insertComplaints(
                listOf(
                    Complaint(
                        ticketNo = "SP-2026-089",
                        flatNo = "Block A-204",
                        residentName = "Rajesh Chodankar",
                        category = ComplaintCategory.ELEVATOR,
                        description = "Elevator sensor in Block A is triggering false door obstruction alarm on 2nd floor.",
                        status = ComplaintStatus.IN_PROGRESS,
                        createdDate = "01 Aug 2026",
                        expectedResolutionDate = "03 Aug 2026",
                        assignedStaff = "KONE Elevator Technician - Suresh",
                        timelineLogsJson = """[{"timestamp":"01 Aug 09:30 AM","statusText":"Ticket Logged","note":"Issue registered by resident"},{"timestamp":"01 Aug 02:15 PM","statusText":"Assigned","note":"Technician visit scheduled for Monday morning"}]"""
                    ),
                    Complaint(
                        ticketNo = "SP-2026-072",
                        flatNo = "Block A-204",
                        residentName = "Rajesh Chodankar",
                        category = ComplaintCategory.PLUMBING,
                        description = "Corridor main valve slight water seepage near meter panel.",
                        status = ComplaintStatus.RESOLVED,
                        createdDate = "20 Jul 2026",
                        expectedResolutionDate = "21 Jul 2026",
                        assignedStaff = "Society Plumber - Ramesh",
                        timelineLogsJson = """[{"timestamp":"20 Jul 10:00 AM","statusText":"Ticket Logged","note":"Reported seepage"},{"timestamp":"20 Jul 04:00 PM","statusText":"In Progress","note":"Plumber replaced washer"},{"timestamp":"21 Jul 11:00 AM","statusText":"Resolved","note":"Inspected and verified leak free"}]"""
                    )
                )
            )

            // Initial Events
            dao.insertEvents(
                listOf(
                    CommunityEvent(
                        title = "Women Empowerment Skill Workshop: Goan Handicrafts & Entrepreneurship",
                        date = "12 Aug 2026",
                        time = "04:00 PM - 07:00 PM",
                        venue = "Society Clubhouse Hall B",
                        description = "Hands-on workshop organized by Sapana Park Women's Cell covering sustainable coconut craft making, home baking, and digital self-reliance.",
                        organizer = "Sapana Women's Cell",
                        rsvpCount = 28,
                        isEmpowermentProgram = true,
                        empowermentCategory = "Women's Skill Workshop",
                        posterUrl = "preset_women_workshop"
                    ),
                    CommunityEvent(
                        title = "Go-Green Solar Energy Drive & E-Waste Collection",
                        date = "16 Aug 2026",
                        time = "09:00 AM - 12:00 PM",
                        venue = "Central Park Lawn",
                        description = "Bring old electronic waste for eco-friendly recycling. Experts will present rooftop solar energy benefits for flat owners.",
                        organizer = "Eco-Green Committee",
                        rsvpCount = 45,
                        isEmpowermentProgram = true,
                        empowermentCategory = "Eco-Green Initiative",
                        posterUrl = "preset_eco_green"
                    ),
                    CommunityEvent(
                        title = "Senior Citizen Health Checkup & Wellness Camp",
                        date = "20 Aug 2026",
                        time = "08:30 AM - 01:30 PM",
                        venue = "Community Health Room",
                        description = "Free BP, blood sugar, ECG, and physiotherapist consultations for senior residents of Sapana Park.",
                        organizer = "Resident Welfare Assoc.",
                        rsvpCount = 34,
                        isEmpowermentProgram = true,
                        empowermentCategory = "Senior Citizen Club",
                        posterUrl = "preset_health_wellness"
                    )
                )
            )

            // Society Documents & Compliance
            dao.insertDocuments(
                listOf(
                    SocietyDocument(
                        title = "Sapana Park Registered Bye-Laws (2022 Amendment)",
                        category = DocumentCategory.BYE_LAWS,
                        description = "Official registered Bye-Laws governing Sapana Park Co-operative Housing Society Ltd under Goa Society Rules.",
                        fileSize = "2.4 MB",
                        dateAdded = "10 Jan 2022",
                        referenceAct = "Reg. No. HSG/G-452/2004"
                    ),
                    SocietyDocument(
                        title = "Goa Co-operative Societies Act, 2001 (Official Reference)",
                        category = DocumentCategory.GOA_SOCIETY_ACT,
                        description = "Statutory compliance guide, rights of flat owners, maintenance recovery procedure, and arbitration rights.",
                        fileSize = "4.8 MB",
                        dateAdded = "15 Mar 2021",
                        referenceAct = "Act 31 of 2001 (Govt. of Goa)"
                    ),
                    SocietyDocument(
                        title = "Standard NOC Request Application for Flat Resale",
                        category = DocumentCategory.NOC_FORM,
                        description = "Required No Objection Certificate form prior to initiating flat purchase/resale agreement.",
                        fileSize = "420 KB",
                        dateAdded = "01 Feb 2024",
                        referenceAct = "Section 73 & Bye-Law 42"
                    ),
                    SocietyDocument(
                        title = "Tenant Verification & Gate Clearance NOC Form",
                        category = DocumentCategory.NOC_FORM,
                        description = "Mandatory form for flat owners leasing out premise to tenants along with Goa Police verification format.",
                        fileSize = "350 KB",
                        dateAdded = "01 Feb 2024",
                        referenceAct = "Security Protocol 2024"
                    ),
                    SocietyDocument(
                        title = "Flat Renovation & Interior Alteration Guidelines",
                        category = DocumentCategory.BYE_LAWS,
                        description = "Permitted working hours, noise control regulations, debris disposal, and structural safety rules.",
                        fileSize = "680 KB",
                        dateAdded = "05 May 2025"
                    )
                )
            )

            // Vehicles
            dao.insertVehicles(
                listOf(
                    Vehicle(
                        registrationNo = "GA-03-AB-4812",
                        flatNo = "Block A-204",
                        vehicleType = "Car (SUV)",
                        parkingSlot = "Slot P-A14"
                    ),
                    Vehicle(
                        registrationNo = "GA-03-Q-9011",
                        flatNo = "Block A-204",
                        vehicleType = "Two-Wheeler",
                        parkingSlot = "Slot B-08"
                    )
                )
            )

            // Initial NOC Requests
            dao.insertNocRequest(
                NocRequest(
                    requestRefNo = "NOC-2026-018",
                    flatNo = "Block A-204",
                    residentName = "Rajesh Chodankar",
                    nocType = "Electricity Meter Name Transfer",
                    reason = "Transferring Electricity Connection meter name after inheritance clearance.",
                    requestedDate = "28 Jul 2026",
                    status = "APPROVED"
                )
            )

            // Initial Saved Consumer Numbers
            dao.saveConsumerNumber(
                SavedConsumerNumber(
                    categoryKey = "ELECTRICITY",
                    consumerNumber = "108293741",
                    billerName = "Goa Electricity Dept (GED)",
                    defaultAmount = 1450.00
                )
            )
            dao.saveConsumerNumber(
                SavedConsumerNumber(
                    categoryKey = "WATER",
                    consumerNumber = "W-482019",
                    billerName = "Public Works Dept (PWD Goa Water)",
                    defaultAmount = 420.00
                )
            )
            dao.saveConsumerNumber(
                SavedConsumerNumber(
                    categoryKey = "HOME_RENT",
                    consumerNumber = "RENT-A204",
                    billerName = "Owner Lease Account",
                    defaultAmount = 15000.00
                )
            )

            // Initial Utility Bills
            dao.insertUtilityBills(
                listOf(
                    UtilityBill(
                        category = UtilityCategory.ELECTRICITY,
                        title = "Electricity Monthly Bill",
                        consumerNumber = "108293741",
                        billerName = "Goa Electricity Dept (GED)",
                        amount = 1450.00,
                        dueDate = "10 Aug 2026",
                        isPaid = false
                    ),
                    UtilityBill(
                        category = UtilityCategory.WATER,
                        title = "PWD Water Supply Bill",
                        consumerNumber = "W-482019",
                        billerName = "Public Works Dept (PWD Goa Water)",
                        amount = 420.00,
                        dueDate = "12 Aug 2026",
                        isPaid = false
                    ),
                    UtilityBill(
                        category = UtilityCategory.MAINTENANCE,
                        title = "Society Monthly Maintenance",
                        consumerNumber = "SP-A204-MAINT",
                        billerName = "Sapana Park CHS Ltd",
                        amount = 2850.00,
                        dueDate = "15 Aug 2026",
                        isPaid = false
                    )
                )
            )
        }
    }
}
