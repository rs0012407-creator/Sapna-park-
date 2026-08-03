package com.example.screens.finance

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.BillCard
import com.example.data.models.BillStatus
import com.example.data.models.MaintenanceBill
import com.example.data.models.UtilityBill
import com.example.data.models.UtilityCategory
import com.example.ui.SapanaParkViewModel
import com.example.utils.FormattingUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    viewModel: SapanaParkViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bills by viewModel.allBills.collectAsState()
    val utilityBills by viewModel.allUtilityBills.collectAsState()
    val savedConsumerNumbers by viewModel.savedConsumerNumbers.collectAsState()
    val profile by viewModel.residentProfile.collectAsState()
    val selectedBillForPayment by viewModel.selectedBillForPayment.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Home Utility Bills, 1: Society Maintenance, 2: Payment History
    var activeUtilityCategory by remember { mutableStateOf(UtilityCategory.ELECTRICITY) }

    var consumerNumberInput by remember { mutableStateOf("") }
    var billerNameInput by remember { mutableStateOf("") }
    var billAmountInput by remember { mutableStateOf("") }
    var applyReferralDiscount by remember { mutableStateOf(false) }
    var selectedPaymentMode by remember { mutableStateOf("UPI_APP") } // UPI_APP, UPI_QR, NET_BANKING, CARD
    var showQrDialog by remember { mutableStateOf(false) }
    var showReceiptModal by remember { mutableStateOf<UtilityBill?>(null) }

    // Auto-fetch saved consumer number details when category changes
    LaunchedEffect(activeUtilityCategory, savedConsumerNumbers) {
        val saved = savedConsumerNumbers.find { it.categoryKey == activeUtilityCategory.name }
        if (saved != null) {
            consumerNumberInput = saved.consumerNumber
            billerNameInput = saved.billerName
            billAmountInput = saved.defaultAmount.toInt().toString()
        } else {
            when (activeUtilityCategory) {
                UtilityCategory.ELECTRICITY -> {
                    consumerNumberInput = "108293741"
                    billerNameInput = "Goa Electricity Dept (GED)"
                    billAmountInput = "1450"
                }
                UtilityCategory.WATER -> {
                    consumerNumberInput = "W-482019"
                    billerNameInput = "Public Works Dept (PWD Goa)"
                    billAmountInput = "420"
                }
                UtilityCategory.HOME_RENT -> {
                    consumerNumberInput = "RENT-A204"
                    billerNameInput = "Owner Lease Account"
                    billAmountInput = "15000"
                }
                UtilityCategory.MAINTENANCE -> {
                    consumerNumberInput = "SP-A204-MAINT"
                    billerNameInput = "Sapana Park CHS Ltd"
                    billAmountInput = "2850"
                }
                UtilityCategory.OTHER -> {
                    consumerNumberInput = "LPG-GAS-8812"
                    billerNameInput = "HP Gas Agency"
                    billAmountInput = "920"
                }
            }
        }
    }

    val pendingMaintenanceBills = bills.filter { it.status == BillStatus.UNPAID || it.status == BillStatus.OVERDUE }
    val paidUtilityBills = utilityBills.filter { it.isPaid }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Finance Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Home Bills & Payment",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Pay Electricity, Water, Rent, Maintenance via UPI/Card",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "₹ INR",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Selector
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("finance_tabs")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Utility Bills", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Maintenance (${pendingMaintenanceBills.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("History (${paidUtilityBills.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Utility Category Chips
                    ScrollableTabRow(
                        selectedTabIndex = activeUtilityCategory.ordinal,
                        edgePadding = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        UtilityCategory.values().forEach { cat ->
                            Tab(
                                selected = activeUtilityCategory == cat,
                                onClick = { activeUtilityCategory = cat },
                                text = {
                                    val title = when(cat) {
                                        UtilityCategory.ELECTRICITY -> "⚡ Electricity"
                                        UtilityCategory.WATER -> "💧 Water"
                                        UtilityCategory.HOME_RENT -> "🏠 Rent"
                                        UtilityCategory.MAINTENANCE -> "🏢 Maintenance"
                                        UtilityCategory.OTHER -> "🔥 LPG / Other"
                                    }
                                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${activeUtilityCategory.name} BILL PAYMENT",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = billerNameInput,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Biller Verified",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Serial / Consumer Number Input (Auto-remembers)
                            OutlinedTextField(
                                value = consumerNumberInput,
                                onValueChange = { input ->
                                    consumerNumberInput = input
                                    // Auto-check if matches saved consumer number
                                    val found = savedConsumerNumbers.find { it.consumerNumber == input }
                                    if (found != null) {
                                        billerNameInput = found.billerName
                                        billAmountInput = found.defaultAmount.toInt().toString()
                                    }
                                },
                                label = { Text("Consumer / Serial / Meter Number") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Saved in Device Memory",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Amount Display / Input
                            OutlinedTextField(
                                value = billAmountInput,
                                onValueChange = { billAmountInput = it },
                                label = { Text("Bill Amount (₹ INR)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Referral Bonus Discount Option
                            val walletBalance = profile?.referralBonusWallet ?: 50.0
                            val parsedAmount = billAmountInput.toDoubleOrNull() ?: 0.0
                            val finalPayable = if (applyReferralDiscount && walletBalance >= 50.0) (parsedAmount - 50.0).coerceAtLeast(0.0) else parsedAmount

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = applyReferralDiscount,
                                            onCheckedChange = { applyReferralDiscount = it },
                                            enabled = walletBalance >= 50.0
                                        )
                                        Text(
                                            text = "Apply ₹50 Referral Bonus Discount",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Text(
                                        text = "Wallet: ₹${walletBalance.toInt()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Select Payment Mode:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedPaymentMode == "UPI_APP",
                                    onClick = { selectedPaymentMode = "UPI_APP" },
                                    label = { Text("Direct UPI App", fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                                FilterChip(
                                    selected = selectedPaymentMode == "UPI_QR",
                                    onClick = { selectedPaymentMode = "UPI_QR" },
                                    label = { Text("Scan QR Code", fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                                FilterChip(
                                    selected = selectedPaymentMode == "CARD",
                                    onClick = { selectedPaymentMode = "CARD" },
                                    label = { Text("Card / NetBank", fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Button
                            Button(
                                onClick = {
                                    val amt = finalPayable
                                    if (amt <= 0) return@Button

                                    // Save consumer number automatically for next time
                                    viewModel.saveConsumerNumber(
                                        categoryKey = activeUtilityCategory.name,
                                        consumerNo = consumerNumberInput,
                                        billerName = billerNameInput,
                                        amount = parsedAmount
                                    )

                                    if (selectedPaymentMode == "UPI_APP") {
                                        // Launch Installed Device UPI App (GPay / PhonePe / Paytm / BHIM)
                                        val upiUri = Uri.parse("upi://pay?pa=sapanapark@upi&pn=SapanaParkCHS&am=$amt&cu=INR&tn=UtilityBill_$consumerNumberInput")
                                        val upiIntent = Intent(Intent.ACTION_VIEW, upiUri)
                                        try {
                                            context.startActivity(Intent.createChooser(upiIntent, "Pay via Device UPI App"))
                                        } catch (e: Exception) {
                                            viewModel.payUtilityBill(
                                                billId = System.currentTimeMillis() % 1000,
                                                method = "UPI (Direct Launch)",
                                                useReferralDiscount = applyReferralDiscount
                                            )
                                        }
                                    } else if (selectedPaymentMode == "UPI_QR") {
                                        showQrDialog = true
                                    } else {
                                        viewModel.payUtilityBill(
                                            billId = System.currentTimeMillis() % 1000,
                                            method = "Credit/Debit Card",
                                            useReferralDiscount = applyReferralDiscount
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pay ₹${finalPayable.toInt()} Now",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                if (pendingMaintenanceBills.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "All Paid",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "All Maintenance Payments Cleared!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(pendingMaintenanceBills, key = { it.id }) { bill ->
                            BillCard(
                                bill = bill,
                                onPayClick = { viewModel.initiatePayment(it) }
                            )
                        }
                    }
                }
            }

            2 -> {
                if (paidUtilityBills.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No payment receipts recorded yet.")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(paidUtilityBills, key = { it.id }) { bill ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showReceiptModal = bill },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = bill.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Ref: ${bill.transactionRef ?: "SP-UPI-881"} • Paid ${bill.paymentDate ?: "Today"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "₹${bill.amount.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dynamic QR Code Generator Modal
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Text(
                    text = "Scan & Pay via Any UPI App",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Scan with GPay, PhonePe, Paytm, or BHIM",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Stylized QR Code Matrix Box
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(3.dp, Color(0xFF1E3A8A), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "UPI QR Code",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(130.dp)
                            )
                            Text(
                                text = "sapanapark@upi",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Amount: ₹${billAmountInput} INR",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showQrDialog = false
                        viewModel.payUtilityBill(
                            billId = System.currentTimeMillis() % 1000,
                            method = "UPI QR Code",
                            useReferralDiscount = applyReferralDiscount
                        )
                    }
                ) {
                    Text("I Have Completed Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Receipt Dialog
    if (showReceiptModal != null) {
        val r = showReceiptModal!!
        AlertDialog(
            onDismissRequest = { showReceiptModal = null },
            title = { Text("Payment Receipt", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Title: ${r.title}", fontWeight = FontWeight.SemiBold)
                    Text("Consumer No: ${r.consumerNumber}")
                    Text("Biller: ${r.billerName}")
                    Text("Amount Paid: ₹${r.amount}")
                    Text("Txn Ref: ${r.transactionRef ?: "SP-UPI-001"}")
                    Text("Status: SUCCESS (PAID)", color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showReceiptModal = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Payment Dialog for Maintenance
    if (selectedBillForPayment != null) {
        PaymentModal(
            bill = selectedBillForPayment!!,
            onDismiss = { viewModel.dismissPaymentModal() },
            onConfirm = { method ->
                viewModel.confirmPayment(selectedBillForPayment!!.id, method)
            }
        )
    }
}

@Composable
fun MaintenanceEstimator(modifier: Modifier = Modifier) {
    var flatAreaSqFt by remember { mutableStateOf("950") }
    var ratePerSqFt by remember { mutableStateOf("2.25") }
    var waterConsumptionUnits by remember { mutableStateOf("150") }

    val area = flatAreaSqFt.toDoubleOrNull() ?: 0.0
    val rate = ratePerSqFt.toDoubleOrNull() ?: 0.0
    val waterUnits = waterConsumptionUnits.toDoubleOrNull() ?: 0.0

    val estimatedBase = area * rate
    val estimatedWater = waterUnits * 2.5
    val sinkingFund = 300.0
    val estimatedTotal = estimatedBase + estimatedWater + sinkingFund

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Sapana Park Maintenance Estimator",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Calculate monthly dues based on flat square footage and water meter units",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = flatAreaSqFt,
                onValueChange = { flatAreaSqFt = it },
                label = { Text("Flat Area (Sq. Ft.)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = ratePerSqFt,
                onValueChange = { ratePerSqFt = it },
                label = { Text("Rate per Sq. Ft. (₹)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = waterConsumptionUnits,
                onValueChange = { waterConsumptionUnits = it },
                label = { Text("Monthly Water Meter Units") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Base Charge (${area.toInt()} sq.ft):")
                Text(FormattingUtils.formatInINR(estimatedBase), fontWeight = FontWeight.SemiBold)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Water Supply (${waterUnits.toInt()} units):")
                Text(FormattingUtils.formatInINR(estimatedWater), fontWeight = FontWeight.SemiBold)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sinking & Repair Fund:")
                Text(FormattingUtils.formatInINR(sinkingFund), fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estimated Monthly Total:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = FormattingUtils.formatInINR(estimatedTotal),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun PaymentModal(
    bill: MaintenanceBill,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI (GPay / PhonePe)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pay Maintenance Bill",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "Flat: ${bill.flatNo} • Bill Period: ${bill.monthYear}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Payable:")
                    Text(
                        text = FormattingUtils.formatInINR(bill.totalAmount),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Select Payment Gateway Mode:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                listOf("UPI (GPay / PhonePe / Paytm)", "Credit / Debit Card", "NetBanking (HDFC / SBI / ICICI)").forEach { method ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedMethod == method) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = method,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedMethod) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Confirm & Pay ${FormattingUtils.formatInINR(bill.totalAmount)}")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
