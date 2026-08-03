package com.example.screens.community

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.models.CommunityEvent
import com.example.ui.SapanaParkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: SapanaParkViewModel,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsState()
    val showAddDialog by viewModel.showAddEventDialog.collectAsState()
    val selectedEventDetails by viewModel.selectedEventForDetails.collectAsState()

    var selectedCategoryTab by remember { mutableStateOf(0) } // 0: Events, 1: Empowerment & Women's Cell, 2: Resident Directory
    var searchQuery by remember { mutableStateOf("") }
    var eventToDelete by remember { mutableStateOf<CommunityEvent?>(null) }

    val filteredEvents = events.filter { event ->
        searchQuery.isBlank() ||
                event.title.contains(searchQuery, ignoreCase = true) ||
                event.venue.contains(searchQuery, ignoreCase = true) ||
                event.organizer.contains(searchQuery, ignoreCase = true)
    }

    val empowermentEvents = filteredEvents.filter { it.isEmpowermentProgram }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF080D18))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sapana Community & Events",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Events posters, full details & past event cleanup",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                if (selectedCategoryTab == 0 || selectedCategoryTab == 1) {
                    Button(
                        onClick = { viewModel.openAddEventDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_event_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Event",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+ Add Event",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedCategoryTab,
                containerColor = Color(0xFF131B2E),
                contentColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("community_tabs")
            ) {
                Tab(
                    selected = selectedCategoryTab == 0,
                    onClick = { selectedCategoryTab = 0 },
                    text = { Text("All Events (${events.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedCategoryTab == 1,
                    onClick = { selectedCategoryTab = 1 },
                    text = { Text("Empowerment", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedCategoryTab == 2,
                    onClick = { selectedCategoryTab = 2 },
                    text = { Text("Directory", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedCategoryTab) {
                0, 1 -> {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search events by name, venue or organizer...", fontSize = 13.sp, color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedContainerColor = Color(0xFF131B2E),
                            unfocusedContainerColor = Color(0xFF131B2E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val displayList = if (selectedCategoryTab == 0) filteredEvents else empowermentEvents

                    if (displayList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF131B2E))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.EventBusy,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No events found",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap '+ Add Event' to post new society event details and posters.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(displayList, key = { it.id }) { event ->
                                EventCardWithPoster(
                                    event = event,
                                    onViewDetails = { viewModel.selectEventDetails(event) },
                                    onDeleteClick = { eventToDelete = event }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // Resident Directory
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search neighbor by name or flat...", fontSize = 13.sp, color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("directory_search_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFF1E293B),
                                focusedContainerColor = Color(0xFF131B2E),
                                unfocusedContainerColor = Color(0xFF131B2E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val sampleNeighbors = listOf(
                            Triple("Rajesh Chodankar", "Block A-204", "Owner • Committee Member"),
                            Triple("Dr. Sunita Prabhu", "Block A-101", "Owner • Medical Doctor"),
                            Triple("Mr. Anand Naik", "Block B-302", "Owner • Society Secretary"),
                            Triple("Peter D'Souza", "Block B-104", "Owner • Treasurer"),
                            Triple("Amina Khan", "Block A-303", "Tenant • Women's Cell Rep"),
                            Triple("Suresh Kamat", "Block B-201", "Owner • Green Committee")
                        ).filter {
                            searchQuery.isBlank() ||
                                    it.first.contains(searchQuery, ignoreCase = true) ||
                                    it.second.contains(searchQuery, ignoreCase = true)
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(sampleNeighbors) { neighbor ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF1E3A8A)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = neighbor.first.take(1),
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = neighbor.first,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${neighbor.second} • ${neighbor.third}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.65f)
                                            )
                                        }

                                        IconButton(onClick = { }) {
                                            Icon(
                                                imageVector = Icons.Default.Call,
                                                contentDescription = "Call",
                                                tint = Color(0xFF10B981)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Event Dialog
        if (showAddDialog) {
            AddEventDialog(
                onDismiss = { viewModel.closeAddEventDialog() },
                onSubmit = { title, date, time, venue, description, organizer, posterPreset, isEmpowerment, empCat, locAddr, locGeo ->
                    viewModel.submitEvent(
                        title = title,
                        date = date,
                        time = time,
                        venue = venue,
                        description = description,
                        organizer = organizer,
                        posterPreset = posterPreset,
                        isEmpowerment = isEmpowerment,
                        empowermentCategory = empCat,
                        locationAddress = locAddr,
                        locationGeoUri = locGeo
                    )
                }
            )
        }

        // Full Event Details Modal
        selectedEventDetails?.let { event ->
            EventDetailsModal(
                event = event,
                onDismiss = { viewModel.selectEventDetails(null) },
                onDelete = {
                    eventToDelete = event
                }
            )
        }

        // Delete Event Confirmation Alert
        eventToDelete?.let { event ->
            AlertDialog(
                onDismissRequest = { eventToDelete = null },
                containerColor = Color(0xFF0F172A),
                icon = {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete Event",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "Delete Event Details?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete '${event.title}'? All event details and poster information will be permanently removed.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val id = event.id
                            val title = event.title
                            eventToDelete = null
                            viewModel.deleteEvent(id, title)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Yes, Delete Event", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { eventToDelete = null }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun EventCardWithPoster(
    event: CommunityEvent,
    onViewDetails: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    var isRsvped by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .testTag("event_card_${event.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // 1. Poster Graphic Banner
            EventPosterBanner(
                event = event,
                onViewDetails = onViewDetails
            )

            // 2. Card Details Content
            Column(modifier = Modifier.padding(16.dp)) {
                // Event Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Organizer Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Organizer",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Organized by: ${event.organizer}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFBBF24),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Venue & Time info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Venue",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.venue,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.time,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Event Location Address Bar with Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .clickable {
                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(event.locationGeoUri))
                            try {
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                // Fallback
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = event.eventLocationAddress.ifBlank { "${event.venue}, Porvorim, North Goa" },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Directions 📍",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF60A5FA),
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Description excerpt
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(12.dp))

                // Footer Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // RSVP Toggle & Count
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { isRsvped = !isRsvped },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isRsvped) Color(0xFF065F46) else Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isRsvped) Icons.Default.CheckCircle else Icons.Default.Event,
                                contentDescription = "RSVP",
                                tint = if (isRsvped) Color(0xFF34D399) else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRsvped) "Attending" else "RSVP Now",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${if (isRsvped) event.rsvpCount + 1 else event.rsvpCount} going",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    // Action Buttons: View Details & Delete Event
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onViewDetails,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Details >",
                                color = Color(0xFF60A5FA),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7F1D1D).copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Event Details",
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventPosterBanner(
    event: CommunityEvent,
    onViewDetails: () -> Unit
) {
    val (posterGradient, posterIcon, presetLabel) = getPosterStyle(event.posterUrl, event.isEmpowermentProgram)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(posterGradient)
            .clickable { onViewDetails() }
            .padding(14.dp)
    ) {
        // Background Decorative Watermark Icon
        Icon(
            imageVector = posterIcon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.12f),
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 20.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Date",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.date,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }

                // Category Poster Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = event.empowermentCategory ?: presetLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }

            // Bottom Poster Branding Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = posterIcon,
                        contentDescription = "Poster Icon",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "SAPANA PARK EVENT POSTER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tap to view full details & schedule",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// Helper to resolve poster gradient styles and icons
private fun getPosterStyle(posterPreset: String?, isEmpowerment: Boolean): Triple<Brush, ImageVector, String> {
    return when (posterPreset) {
        "preset_women_workshop" -> Triple(
            Brush.linearGradient(listOf(Color(0xFF831843), Color(0xFFBE185D), Color(0xFFDB2777))),
            Icons.Default.Female,
            "WOMEN'S CELL POSTER"
        )
        "preset_eco_green" -> Triple(
            Brush.linearGradient(listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981))),
            Icons.Default.Park,
            "ECO-GREEN DRIVE POSTER"
        )
        "preset_health_wellness" -> Triple(
            Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0284C7))),
            Icons.Default.HealthAndSafety,
            "HEALTH CAMP POSTER"
        )
        "preset_cultural" -> Triple(
            Brush.linearGradient(listOf(Color(0xFF78350F), Color(0xFFD97706), Color(0xFFF59E0B))),
            Icons.Default.Celebration,
            "CULTURAL FESTIVAL POSTER"
        )
        "preset_meeting" -> Triple(
            Brush.linearGradient(listOf(Color(0xFF311B92), Color(0xFF4A148C), Color(0xFF7B1FA2))),
            Icons.Default.Gavel,
            "SOCIETY MEETING POSTER"
        )
        else -> if (isEmpowerment) {
            Triple(
                Brush.linearGradient(listOf(Color(0xFF831843), Color(0xFF9D174D), Color(0xFF0284C7))),
                Icons.Default.Stars,
                "EMPOWERMENT POSTER"
            )
        } else {
            Triple(
                Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF1E3A8A), Color(0xFF2563EB))),
                Icons.Default.Event,
                "SOCIETY EVENT POSTER"
            )
        }
    }
}

// Full Event Details Modal Dialog
@Composable
fun EventDetailsModal(
    event: CommunityEvent,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var isRsvped by remember { mutableStateOf(false) }
    val (posterGradient, posterIcon, presetLabel) = getPosterStyle(event.posterUrl, event.isEmpowermentProgram)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(22.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Large Poster Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(posterGradient)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = posterIcon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 30.dp, y = 30.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = presetLabel,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White,
                            fontSize = 17.sp,
                            maxLines = 2
                        )
                    }
                }

                // Event Info Matrix
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailRow(icon = Icons.Default.CalendarToday, label = "Date", value = event.date)
                        DetailRow(icon = Icons.Default.Schedule, label = "Timing", value = event.time)
                        DetailRow(icon = Icons.Default.Place, label = "Venue", value = event.venue)
                        DetailRow(
                            icon = Icons.Default.LocationOn,
                            label = "Event Location Address",
                            value = event.eventLocationAddress.ifBlank { "${event.venue}, Porvorim, North Goa" }
                        )
                        DetailRow(icon = Icons.Default.Groups, label = "Organizer", value = event.organizer)
                        if (event.isEmpowermentProgram) {
                            DetailRow(icon = Icons.Default.Star, label = "Category", value = event.empowermentCategory ?: "Empowerment Program")
                        }
                    }
                }

                // Map Navigation Button
                Button(
                    onClick = {
                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(event.locationGeoUri))
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Directions in Google Maps 📍", fontWeight = FontWeight.Bold)
                }

                // Full Description Section
                Text(
                    text = "Event Overview & Agenda",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    fontSize = 14.sp
                )

                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Attendance RSVP
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Resident Participation",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "${if (isRsvped) event.rsvpCount + 1 else event.rsvpCount} Neighbors Attending",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF10B981)
                        )
                    }

                    Button(
                        onClick = { isRsvped = !isRsvped },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRsvped) Color(0xFF065F46) else Color(0xFF2563EB)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isRsvped) "Attending ✓" else "RSVP Now", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Delete Event Details Action Box (For completed or past events)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF451A03).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Event Finished or Over?",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFDBA74),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Remove details & poster from society board",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Details", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.White)
            }
        }
    )
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 10.sp)
            Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White, fontSize = 13.sp)
        }
    }
}

// Add Event Dialog with Poster Preset Selection
@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        date: String,
        time: String,
        venue: String,
        description: String,
        organizer: String,
        posterPreset: String?,
        isEmpowerment: Boolean,
        empowermentCategory: String?,
        locationAddress: String,
        locationGeoUri: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var venue by remember { mutableStateOf("Society Clubhouse") }
    var locationAddress by remember { mutableStateOf("Sapana Park CHS Lawn, Porvorim, North Goa") }
    var locationGeoUri by remember { mutableStateOf("geo:15.5262,73.8315?q=Sapana+Park+CHS+Porvorim") }
    var organizer by remember { mutableStateOf("Cultural Club") }
    var description by remember { mutableStateOf("") }

    var selectedPosterPreset by remember { mutableStateOf("preset_cultural") }
    var isEmpowerment by remember { mutableStateOf(false) }
    var empowermentCategory by remember { mutableStateOf("Women's Skill Workshop") }

    val posterOptions = listOf(
        Triple("preset_cultural", "Cultural Festival", Icons.Default.Celebration),
        Triple("preset_women_workshop", "Women's Workshop", Icons.Default.Female),
        Triple("preset_eco_green", "Eco-Green Drive", Icons.Default.Park),
        Triple("preset_health_wellness", "Health & Wellness", Icons.Default.HealthAndSafety),
        Triple("preset_meeting", "General Meeting", Icons.Default.Gavel)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.PostAdd, contentDescription = null, tint = Color(0xFF2563EB))
                Text("Add Event Details & Poster", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Event Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title *", fontSize = 12.sp) },
                    placeholder = { Text("e.g., Independence Day Music Night", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Date & Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date *", fontSize = 12.sp) },
                        placeholder = { Text("15 Aug 2026", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time *", fontSize = 12.sp) },
                        placeholder = { Text("05:00 PM", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                // Venue & Organizer Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Venue", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = organizer,
                        onValueChange = { organizer = it },
                        label = { Text("Organizer", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                // Event Location Address & Map Pin Input
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = locationAddress,
                        onValueChange = {
                            locationAddress = it
                            if (it.isNotBlank()) {
                                locationGeoUri = "geo:15.5262,73.8315?q=${it.replace(" ", "+")}"
                            }
                        },
                        label = { Text("Event Location Address 📍", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Sapana Park CHS Clubhouse Lawn, Porvorim", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(Icons.Default.Place, contentDescription = "Location", tint = Color(0xFF38BDF8))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        SuggestionChip(
                            onClick = {
                                locationAddress = "Sapana Park CHS Main Lawn, Near Kadamba Bus Stand, Porvorim, North Goa"
                                locationGeoUri = "geo:15.5262,73.8315?q=Sapana+Park+CHS+Porvorim"
                            },
                            label = { Text("📍 Auto-Detect Sapana Park GPS Pin", fontSize = 10.sp, color = Color(0xFF38BDF8)) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }

                // Poster Theme Style Selector
                Text(
                    text = "Select Event Poster Banner Style *",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    fontSize = 12.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    posterOptions.forEach { option ->
                        val isSelected = selectedPosterPreset == option.first
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedPosterPreset = option.first },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF1E293B)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = option.third,
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF60A5FA) else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = option.second,
                                    color = if (isSelected) Color.White else Color.LightGray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color(0xFF60A5FA))
                                }
                            }
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Event Description & Details", fontSize = 12.sp) },
                    placeholder = { Text("Enter detailed agenda, entry rules or chief guest details...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Empowerment Program Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Empowerment / Skill Initiative", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Tag for Women's Cell, Eco Drive or Senior Citizen", color = Color.Gray, fontSize = 10.sp)
                    }
                    Switch(
                        checked = isEmpowerment,
                        onCheckedChange = { isEmpowerment = it }
                    )
                }

                if (isEmpowerment) {
                    OutlinedTextField(
                        value = empowermentCategory,
                        onValueChange = { empowermentCategory = it },
                        label = { Text("Empowerment Category Tag", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Women's Skill Workshop", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && date.isNotBlank()) {
                        onSubmit(
                            title,
                            date,
                            if (time.isBlank()) "05:00 PM" else time,
                            venue,
                            if (description.isBlank()) "Join us for this exciting society event!" else description,
                            organizer,
                            selectedPosterPreset,
                            isEmpowerment,
                            empowermentCategory,
                            locationAddress,
                            locationGeoUri
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                enabled = title.isNotBlank() && date.isNotBlank()
            ) {
                Text("Publish Event & Poster", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}
