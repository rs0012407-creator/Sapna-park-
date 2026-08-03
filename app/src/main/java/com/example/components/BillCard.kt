package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BillStatus
import com.example.data.models.MaintenanceBill
import com.example.utils.FormattingUtils

@Composable
fun BillCard(
    bill: MaintenanceBill,
    onPayClick: (MaintenanceBill) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bill_card_${bill.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                when (bill.status) {
                                    BillStatus.UNPAID -> MaterialTheme.colorScheme.errorContainer
                                    BillStatus.PAID -> MaterialTheme.colorScheme.primaryContainer
                                    BillStatus.OVERDUE -> MaterialTheme.colorScheme.tertiaryContainer
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (bill.status) {
                                BillStatus.UNPAID -> Icons.Default.ReceiptLong
                                BillStatus.PAID -> Icons.Default.CheckCircle
                                BillStatus.OVERDUE -> Icons.Default.Warning
                            },
                            contentDescription = "Bill Icon",
                            tint = when (bill.status) {
                                BillStatus.UNPAID -> MaterialTheme.colorScheme.onErrorContainer
                                BillStatus.PAID -> MaterialTheme.colorScheme.onPrimaryContainer
                                BillStatus.OVERDUE -> MaterialTheme.colorScheme.onTertiaryContainer
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = bill.monthYear,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Due: ${bill.dueDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusPill(status = bill.status)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = FormattingUtils.formatInINR(bill.totalAmount),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(if (isExpanded) "Hide Breakdown" else "View Breakdown")
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Breakdown"
                        )
                    }

                    if (bill.status == BillStatus.UNPAID || bill.status == BillStatus.OVERDUE) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onPayClick(bill) },
                            modifier = Modifier.testTag("pay_bill_button_${bill.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = "Pay")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pay Now")
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Bill Items Breakdown (${bill.billNumber})",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BreakdownRow("Base Maintenance Charges", bill.baseMaintenance)
                    BreakdownRow("Water Supply Charges", bill.waterCharges)
                    BreakdownRow("Sinking & Repair Fund", bill.sinkingFund)
                    if (bill.lateFee > 0) {
                        BreakdownRow("Late Payment Fee", bill.lateFee)
                    }

                    if (bill.status == BillStatus.PAID && bill.paymentDate != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Paid on: ${bill.paymentDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Ref: ${bill.transactionRef ?: "N/A"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = FormattingUtils.formatInINR(amount),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StatusPill(status: BillStatus) {
    val (bgColor, textColor, text) = when (status) {
        BillStatus.UNPAID -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "UNPAID")
        BillStatus.PAID -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), "PAID")
        BillStatus.OVERDUE -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), "OVERDUE")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}
