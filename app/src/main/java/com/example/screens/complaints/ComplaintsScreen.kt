package com.example.screens.complaints

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.ComplaintCard
import com.example.data.models.Complaint
import com.example.data.models.ComplaintCategory
import com.example.data.models.ComplaintStatus
import com.example.ui.SapanaParkViewModel

@Composable
fun ComplaintsScreen(
    viewModel: SapanaParkViewModel,
    modifier: Modifier = Modifier
) {
    val complaints by viewModel.allComplaints.collectAsState()
    val showNewDialog by viewModel.showNewComplaintDialog.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Active Complaints, 1: Complaint History
    var selectedFilterCategory by remember { mutableStateOf<ComplaintCategory?>(null) }

    val activeComplaints = complaints.filter {
        it.status == ComplaintStatus.OPEN || it.status == ComplaintStatus.IN_PROGRESS
    }
    val historyComplaints = complaints.filter {
        it.status == ComplaintStatus.RESOLVED || it.status == ComplaintStatus.CLOSED
    }

    val currentTabComplaints = if (selectedTabIndex == 0) activeComplaints else historyComplaints

    val filteredComplaints = currentTabComplaints.filter {
        selectedFilterCategory == null || it.category == selectedFilterCategory
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openNewComplaintDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = "Log Complaint") },
                text = { Text("Log Complaint") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_log_complaint")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Grievance & Service Requests",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Track resolution status & photo evidence with Sapana Park team",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Row: Active Complaints vs Complaint History
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "Active (${activeComplaints.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "History & Resolved (${historyComplaints.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Row
            ScrollableTabRow(
                selectedTabIndex = if (selectedFilterCategory == null) 0 else selectedFilterCategory!!.ordinal + 1,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedFilterCategory == null,
                    onClick = { selectedFilterCategory = null },
                    text = { Text("All (${currentTabComplaints.size})") }
                )
                ComplaintCategory.values().forEach { category ->
                    val count = currentTabComplaints.count { it.category == category }
                    Tab(
                        selected = selectedFilterCategory == category,
                        onClick = { selectedFilterCategory = category },
                        text = { Text("${category.name} ($count)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredComplaints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "No Issues",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTabIndex == 0) "No active complaints in this category" else "No complaint history recorded",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredComplaints, key = { it.id }) { complaint ->
                        ComplaintCard(
                            complaint = complaint,
                            onDeleteComplaint = { target ->
                                viewModel.deleteComplaint(target.id, target.ticketNo)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showNewDialog) {
        NewComplaintDialog(
            onDismiss = { viewModel.closeNewComplaintDialog() },
            onSubmit = { category, description, photoUri ->
                viewModel.submitComplaintWithPhoto(category, description, photoUri)
                viewModel.closeNewComplaintDialog()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewComplaintDialog(
    onDismiss: () -> Unit,
    onSubmit: (ComplaintCategory, String, String?) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ComplaintCategory.PLUMBING) }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Media / Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Lodge Maintenance Complaint",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "Select Category:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))

                var expandedCategoryMenu by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedCategoryMenu,
                    onExpandedChange = { expandedCategoryMenu = !expandedCategoryMenu }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryMenu) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoryMenu,
                        onDismissRequest = { expandedCategoryMenu = false }
                    ) {
                        ComplaintCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    expandedCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Describe the maintenance issue...") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complaint_description_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Camera & Gallery Permission / Photo attachment
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (photoUri != null) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                            contentDescription = "Attach Evidence Photo"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (photoUri != null) "Photo Attached from Device Gallery" else "Attach Photo Evidence (Camera/Gallery)")
                    }

                    if (photoUri != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Attached: ${photoUri.toString().takeLast(25)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        onSubmit(selectedCategory, description, photoUri?.toString())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("submit_complaint_button")
            ) {
                Text("Submit Ticket")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
