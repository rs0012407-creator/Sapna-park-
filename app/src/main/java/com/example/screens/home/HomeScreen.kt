package com.example.screens.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.BillCard
import com.example.components.NoticeCard
import com.example.data.models.BillStatus
import com.example.ui.SapanaParkViewModel
import com.example.utils.FormattingUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SapanaParkViewModel,
    onNavigateToTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by viewModel.residentProfile.collectAsState()
    val bills by viewModel.allBills.collectAsState()
    val notices by viewModel.allNotices.collectAsState()
    val complaints by viewModel.allComplaints.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    val pendingBill = bills.firstOrNull { it.status == BillStatus.UNPAID || it.status == BillStatus.OVERDUE }

    var showAiMitrSheet by remember { mutableStateOf(false) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showByeLawsDialog by remember { mutableStateOf(false) }
    var showCommitteeDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF080D18))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Network Connectivity Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isOnline) Color(0xFF065F46) else Color(0xFF991B1B)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isOnline) "🟢 Device Internet Active — Live Sync On" else "⚡ Offline Mode — Local Room Database Active",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    TextButton(
                        onClick = {
                            viewModel.refreshAllData()
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sync Now",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 1. Welcome Header Card (Royal Blue Gradient Card) with Share Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_welcome_card"),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1D4ED8),
                                    Color(0xFF2563EB),
                                    Color(0xFF0284C7)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Welcome Back,",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = profile?.name ?: "Rajesh Chodankar",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${profile?.flatNo ?: "Block A-204"} • Member (Owner)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFFFBBF24), // Golden Amber accent
                                    fontSize = 13.sp
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Share App Button
                                IconButton(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Download Sapana Park CHS App! Use my referral code '${profile?.referralCode ?: "SAPANA50"}' to register and claim ₹50 Bonus + 2% Discount on Home Utility Bills!"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Sapana Park App"))
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share App",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                // Apartment Icon Badge
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Apartment,
                                        contentDescription = "Society Home",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Verified Badge & Society Pill Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF10B981), // Emerald checkmark
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Aadhaar & KYC Verified",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF59E0B))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "Sapana Park CHS",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color(0xFF0F172A),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Share & Earn Referral Reward Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_referral_reward_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)), // Royal Indigo
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF818CF8).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Referral Wallet",
                                tint = Color(0xFFA5B4FC),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Referral Bonus Wallet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Balance: ₹${profile?.referralBonusWallet ?: 50.0} • Code: ${profile?.referralCode ?: "SAPANA50"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC7D2FE),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.claimReferralBonus()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Claim +₹50", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // 2. Sapana Park AI Mitr Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { showAiMitrSheet = true }
                    .testTag("home_ai_mitr_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B2E22)), // Dark Forest Green
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Gold Circular Avatar Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAB308)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Mitr",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Sapana Park AI Mitr",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEAB308))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "AI ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = Color(0xFF0F172A),
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "24x7 Assistant for Events, Bills, Rules & Issues",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open AI Mitr",
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 3. Maintenance Dues Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_maintenance_dues_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2436)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Due Alert",
                                tint = Color(0xFFEF4444), // Red Warning
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Maintenance Dues: August 2026",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Amount: ₹500 • Due: August 15, 2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            pendingBill?.let { viewModel.initiatePayment(it) } ?: onNavigateToTab(1)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)), // Green
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("pay_now_button")
                    ) {
                        Text(
                            text = "Pay Now",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            // 4. Quick Actions Grid Section
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            // 2 Rows x 4 Columns Quick Actions Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionGridCard(
                        icon = Icons.Default.ReceiptLong,
                        iconTint = Color(0xFF60A5FA),
                        iconBg = Color(0xFF1E3A8A),
                        label = "Pay Bills",
                        onClick = { onNavigateToTab(1) },
                        modifier = Modifier.weight(1f).testTag("qa_pay_bills")
                    )
                    QuickActionGridCard(
                        icon = Icons.Default.Build,
                        iconTint = Color(0xFFFB923C),
                        iconBg = Color(0xFF7C2D12),
                        label = "New Com...",
                        onClick = {
                            viewModel.openNewComplaintDialog()
                            onNavigateToTab(2)
                        },
                        modifier = Modifier.weight(1f).testTag("qa_new_complaint")
                    )
                    QuickActionGridCard(
                        icon = Icons.Default.Campaign,
                        iconTint = Color(0xFF2DD4BF),
                        iconBg = Color(0xFF134E4A),
                        label = "Notices",
                        onClick = { onNavigateToTab(0) },
                        modifier = Modifier.weight(1f).testTag("qa_notices")
                    )
                    QuickActionGridCard(
                        icon = Icons.Default.Emergency,
                        iconTint = Color(0xFFF87171),
                        iconBg = Color(0xFF7F1D1D),
                        label = "Emergency",
                        onClick = { showEmergencyDialog = true },
                        modifier = Modifier.weight(1f).testTag("qa_emergency")
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionGridCard(
                        icon = Icons.Default.Groups,
                        iconTint = Color(0xFF818CF8),
                        iconBg = Color(0xFF1E1B4B),
                        label = "Committee",
                        onClick = { showCommitteeDialog = true },
                        modifier = Modifier.weight(1f).testTag("qa_committee")
                    )
                    QuickActionGridCard(
                        icon = Icons.Default.CalendarMonth,
                        iconTint = Color(0xFF38BDF8),
                        iconBg = Color(0xFF164E63),
                        label = "Events",
                        onClick = { onNavigateToTab(3) },
                        modifier = Modifier.weight(1f).testTag("qa_events")
                    )
                    QuickActionGridCard(
                        icon = Icons.Default.MenuBook,
                        iconTint = Color(0xFF94A3B8),
                        iconBg = Color(0xFF334155),
                        label = "Bye-Laws",
                        onClick = { showByeLawsDialog = true },
                        modifier = Modifier.weight(1f).testTag("qa_byelaws")
                    )
                    QuickActionGridCard(
                        icon = Icons.Default.Assignment,
                        iconTint = Color(0xFFFBBF24),
                        iconBg = Color(0xFF78350F),
                        label = "Complaints",
                        onClick = { onNavigateToTab(2) },
                        modifier = Modifier.weight(1f).testTag("qa_complaints")
                    )
                }
            }

            // 5. Colony & Home Complaint Box Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        viewModel.openNewComplaintDialog()
                        onNavigateToTab(2)
                    }
                    .testTag("home_complaint_box_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Orange Megaphone Circle Badge
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEA580C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Complaint Box",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Colony & Home Complaint Box",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Publicly visible complaints & photo evidence",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEA580C).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Add Evidence",
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Notice Board Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Society Notice Board",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            if (notices.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    notices.take(2).forEach { notice ->
                        NoticeCard(notice = notice)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // AI Mitr Dialog / Modal
        if (showAiMitrSheet) {
            AiMitrDialog(onDismiss = { showAiMitrSheet = false })
        }

        // Emergency Contacts Dialog
        if (showEmergencyDialog) {
            EmergencyContactsDialog(onDismiss = { showEmergencyDialog = false })
        }

        // Bye-Laws Dialog
        if (showByeLawsDialog) {
            ByeLawsDialog(onDismiss = { showByeLawsDialog = false })
        }

        // Committee Dialog
        if (showCommitteeDialog) {
            CommitteeDialog(onDismiss = { showCommitteeDialog = false })
        }
    }
}

@Composable
private fun QuickActionGridCard(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(92.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AiMitrDialog(onDismiss: () -> Unit) {
    var userPrompt by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            "Namaste! I am Sapana Park AI Mitr 🤖. How can I help you today with maintenance bills, clubhouse bookings, or society rules?"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D172A),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAB308)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI Mitr",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text("Sapana Park AI Mitr", color = Color.White, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("AI Society Assistant", color = Color(0xFFEAB308), style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chatMessages.forEachIndexed { idx, msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (idx % 2 == 0) Color(0xFF1E293B) else Color(0xFF1E3A8A))
                                .padding(10.dp)
                        ) {
                            Text(text = msg, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = userPrompt,
                        onValueChange = { userPrompt = it },
                        placeholder = { Text("Ask about bills, rules, complaints...", fontSize = 12.sp, color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEAB308),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    IconButton(
                        onClick = {
                            if (userPrompt.isNotBlank()) {
                                val query = userPrompt
                                chatMessages.add("You: $query")
                                userPrompt = ""

                                val reply = when {
                                    query.contains("bill", ignoreCase = true) || query.contains("dues", ignoreCase = true) ->
                                        "AI Mitr: Your August 2026 Maintenance Bill is ₹500. Due date is August 15, 2026. You can pay via UPI directly in the app."
                                    query.contains("rule", ignoreCase = true) || query.contains("timing", ignoreCase = true) ->
                                        "AI Mitr: Society Rules: Silence hours 10 PM - 6 AM. Swimming pool open 6 AM - 10 AM & 4 PM - 8 PM. Visitors must check-in at Sapana Gate."
                                    query.contains("complaint", ignoreCase = true) ->
                                        "AI Mitr: You can log complaints under plumbing, electrical, or security. Escalation window is 24-48 hours."
                                    else ->
                                        "AI Mitr: I have logged your inquiry for $query. Managing Committee desk has been notified."
                                }
                                chatMessages.add(reply)
                            }
                        },
                        modifier = Modifier.background(Color(0xFFEAB308), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF0F172A))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFFEAB308))
            }
        }
    )
}

@Composable
private fun EmergencyContactsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D172A),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Emergency, contentDescription = null, tint = Color(0xFFEF4444))
                Text("Emergency Contacts", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                EmergencyContactRow("Main Gate Security Desk", "+91 832 2456789", "24/7 Gate Patrol")
                EmergencyContactRow("Society Manager (Mr. Naik)", "+91 98230 11223", "Emergency Supervisor")
                EmergencyContactRow("Fire & Medical Helpline", "112 / 108", "National Emergency")
                EmergencyContactRow("Lift Maintenance Duty", "+91 98221 00998", "Otis / Schindler Desk")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = Color.White) }
        }
    )
}

@Composable
private fun EmergencyContactRow(name: String, number: String, role: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(role, color = Color.Gray, fontSize = 11.sp)
            }
            Text(number, color = Color(0xFF60A5FA), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ByeLawsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D172A),
        title = {
            Text("Sapana Park CHS Bye-Laws", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("1. Maintenance payments must be cleared before the 15th of every month.", color = Color.LightGray, fontSize = 12.sp)
                Text("2. Parking is strictly allotted per flat registration.", color = Color.LightGray, fontSize = 12.sp)
                Text("3. Renovation noise restricted to 9 AM - 6 PM on weekdays.", color = Color.LightGray, fontSize = 12.sp)
                Text("4. Pet animals must be leashed in common areas & gardens.", color = Color.LightGray, fontSize = 12.sp)
                Text("5. Garbage segregation (wet & dry) is mandatory.", color = Color.LightGray, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got It", color = Color(0xFF38BDF8)) }
        }
    )
}

@Composable
private fun CommitteeDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D172A),
        title = {
            Text("Managing Committee (2026-2028)", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CommitteeMemberRow("Mrs. Sunita Prabhu", "Society Chairman", "+91 98221 33445")
                CommitteeMemberRow("Mr. Anand Naik", "Secretary", "+91 98230 11223")
                CommitteeMemberRow("Mr. Peter D'Souza", "Treasurer", "+91 98225 66778")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = Color.White) }
        }
    )
}

@Composable
private fun CommitteeMemberRow(name: String, title: String, phone: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(title, color = Color(0xFF818CF8), fontSize = 11.sp)
            Text(phone, color = Color.Gray, fontSize = 11.sp)
        }
    }
}
