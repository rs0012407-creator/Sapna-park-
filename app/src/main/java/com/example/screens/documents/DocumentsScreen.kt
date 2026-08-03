package com.example.screens.documents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.components.DocumentCard
import com.example.data.models.NocRequest
import com.example.data.models.SocietyDocument
import com.example.ui.SapanaParkViewModel
import com.example.utils.GoaSocietyActCompliance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    viewModel: SapanaParkViewModel,
    modifier: Modifier = Modifier
) {
    val documents by viewModel.allDocuments.collectAsState()
    val nocRequests by viewModel.allNocRequests.collectAsState()
    val showNocDialog by viewModel.showNocRequestDialog.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Society Documents, 1: My NOC Applications, 2: Goa Act Compliance Reference

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Society Documents & NOCs",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Bye-Laws, NOC Forms & Goa Housing Society Act 2001",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { viewModel.openNocDialog() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("apply_noc_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Apply")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Apply NOC")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("documents_tabs")
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Bye-Laws & Forms") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("NOC Requests (${nocRequests.size})") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Goa Society Act") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(documents, key = { it.id }) { doc ->
                        DocumentCard(
                            document = doc,
                            onDownloadClick = {
                                // Download simulation
                            }
                        )
                    }
                }
            }

            1 -> {
                if (nocRequests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No NOC applications submitted yet.")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(nocRequests, key = { it.id }) { noc ->
                            NocRequestCard(noc = noc)
                        }
                    }
                }
            }

            2 -> {
                GoaActReferenceCard(modifier = Modifier.weight(1f))
            }
        }
    }

    if (showNocDialog) {
        InteractiveNocRequestDialog(
            onDismiss = { viewModel.closeNocDialog() },
            onSubmit = { nocType, reason ->
                viewModel.submitNoc(nocType, reason)
            }
        )
    }
}

@Composable
fun NocRequestCard(noc: NocRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = noc.requestRefNo,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when (noc.status) {
                                "APPROVED" -> Color(0xFFDCFCE7)
                                "REJECTED" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFFEF3C7)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = noc.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when (noc.status) {
                            "APPROVED" -> Color(0xFF166534)
                            "REJECTED" -> Color(0xFF991B1B)
                            else -> Color(0xFF92400E)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = noc.nocType,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Reason: ${noc.reason}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Requested on: ${noc.requestedDate} • Flat: ${noc.flatNo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun GoaActReferenceCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "Act",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = GoaSocietyActCompliance.ACT_TITLE,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Statutory Rights & Statutory Compliance Summary",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            GoaSocietyActCompliance.SUMMARY_POINTS.forEachIndexed { index, point ->
                Row(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveNocRequestDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var selectedNocType by remember { mutableStateOf("Flat Resale / Transfer NOC") }
    var reason by remember { mutableStateOf("") }

    val nocTypes = listOf(
        "Flat Resale / Transfer NOC",
        "Tenant Verification & Lease NOC",
        "Flat Renovation / Structural Work NOC",
        "Electricity Meter Name Transfer NOC",
        "Bank Home Loan No Dues NOC"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Generate Society NOC Request",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "Select NOC Type:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                var expandedNocMenu by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedNocMenu,
                    onExpandedChange = { expandedNocMenu = !expandedNocMenu }
                ) {
                    OutlinedTextField(
                        value = selectedNocType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedNocMenu) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedNocMenu,
                        onDismissRequest = { expandedNocMenu = false }
                    ) {
                        nocTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedNocType = type
                                    expandedNocMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason / Purpose of NOC") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("noc_reason_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isNotBlank()) {
                        onSubmit(selectedNocType, reason)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("submit_noc_button")
            ) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
