package com.example.screens.auth

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthState
import com.example.ui.SapanaParkViewModel
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    viewModel: SapanaParkViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val authState by viewModel.authState.collectAsState()

    var phone by remember { mutableStateOf("9822145678") }
    var otpCode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("Rajesh Chodankar") }
    var roomNumber by remember { mutableStateOf("204") }
    var floorNumber by remember { mutableStateOf("2nd Floor") }
    var email by remember { mutableStateOf("rajesh.chodankar@sapanapark.org") }
    var isOwner by remember { mutableStateOf(true) }
    var showFirebaseHelp by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // App Brand Logo Container
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apartment,
                    contentDescription = "Sapana Park Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Sapana Park CHS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Firebase Phone Authentication & Firestore Registration",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    when (val current = authState) {
                        is AuthState.LoggedOut, is AuthState.SendingOtp -> {
                            Text(
                                text = "Resident Mobile Registration",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Enter mobile number & room details for Firebase OTP",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Name
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("name_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Mobile Number
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Mobile Number (e.g. 9822145678)") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                                },
                                prefix = { Text("+91 ") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Room Number & Floor Number Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = roomNumber,
                                    onValueChange = { roomNumber = it },
                                    label = { Text("Room No.") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null)
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("room_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = floorNumber,
                                    onValueChange = { floorNumber = it },
                                    label = { Text("Floor No.") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Layers, contentDescription = null)
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("floor_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Email
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("email_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Resident Type Chip Choice
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Resident Type:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    FilterChip(
                                        selected = isOwner,
                                        onClick = { isOwner = true },
                                        label = { Text("Flat Owner") }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    FilterChip(
                                        selected = !isOwner,
                                        onClick = { isOwner = false },
                                        label = { Text("Tenant") }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            val isLoading = current is AuthState.SendingOtp
                            Button(
                                onClick = { viewModel.sendOtp(phone, activity) },
                                enabled = !isLoading && phone.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("send_otp_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending SMS OTP...")
                                } else {
                                    Icon(imageVector = Icons.Default.Sms, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Send 6-Digit OTP",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        is AuthState.OtpSent, is AuthState.VerifyingOtp -> {
                            val otpSentState = current as? AuthState.OtpSent
                            val targetPhone = otpSentState?.phone ?: phone

                            var timerSeconds by remember { mutableIntStateOf(60) }
                            var canResend by remember { mutableStateOf(false) }

                            LaunchedEffect(targetPhone) {
                                timerSeconds = 60
                                canResend = false
                                while (timerSeconds > 0) {
                                    delay(1000L)
                                    timerSeconds--
                                }
                                canResend = true
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Verify 6-Digit OTP",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                TextButton(onClick = { viewModel.logout() }) {
                                    Text("Edit Phone")
                                }
                            }

                            Text(
                                text = "OTP code sent via SMS to $targetPhone",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (otpSentState?.isFallbackMode == true) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ) {
                                    Text(
                                        text = "💡 Demo Mode Active: Use code '123456' or any 6-digit code to verify & test Firestore saving.",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 6-Digit Visual Box Display
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (i in 0 until 6) {
                                    val digit = if (i < otpCode.length) otpCode[i].toString() else ""
                                    val isCurrentDigit = otpCode.length == i
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .border(
                                                width = if (isCurrentDigit) 2.dp else 1.dp,
                                                color = if (isCurrentDigit) MaterialTheme.colorScheme.primary else Color.LightGray,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .background(
                                                if (isCurrentDigit) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = digit,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Text Input for 6-Digit OTP
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = {
                                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                        otpCode = it
                                    }
                                },
                                label = { Text("Enter 6-Digit OTP Code") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Resend OTP Section with 60s Countdown
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (timerSeconds > 0) "Resend code in ${timerSeconds}s" else "Didn't receive code?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                TextButton(
                                    onClick = {
                                        viewModel.resendOtp(targetPhone, activity)
                                        timerSeconds = 60
                                        canResend = false
                                    },
                                    enabled = canResend
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Resend OTP",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val isVerifying = current is AuthState.VerifyingOtp
                            Button(
                                onClick = {
                                    viewModel.verifyOtpAndRegisterUser(
                                        activity = activity,
                                        otp = otpCode,
                                        name = name,
                                        roomNumber = roomNumber,
                                        floorNumber = floorNumber,
                                        email = email,
                                        isOwner = isOwner
                                    )
                                },
                                enabled = !isVerifying && otpCode.length == 6,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("verify_otp_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isVerifying) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verifying & Registering...")
                                } else {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Verify OTP & Complete Registration",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        is AuthState.LoggedIn -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Logged In",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Account Logged In & Synced",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${current.profile.name} • Room ${current.profile.roomNumber}, ${current.profile.floorNumber}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Mobile: ${current.profile.phone} | Email: ${current.profile.email}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = { viewModel.logout() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Log Out")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Developer Firebase Setup & Configuration Guide Toggle Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showFirebaseHelp = !showFirebaseHelp },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Firebase Info",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Firebase Phone Auth & Firestore Guide",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Icon(
                            imageVector = if (showFirebaseHelp) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = Color.White
                        )
                    }

                    AnimatedVisibility(visible = showFirebaseHelp) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "1. Add google-services.json to app/ directory.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "2. Enable Phone Provider in Firebase Console -> Auth -> Sign-in Method.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "3. Create Firestore database in Firebase Console -> Firestore Database.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = "4. User data is written to Firestore 'users' collection with fields: name, mobileNumber, roomNumber, floorNumber, email, registrationDate.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
