package com.example.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.screens.auth.AuthScreen
import com.example.screens.community.CommunityScreen
import com.example.screens.complaints.ComplaintsScreen
import com.example.screens.documents.DocumentsScreen
import com.example.screens.finance.FinanceScreen
import com.example.screens.home.HomeScreen
import com.example.screens.profile.ProfileScreen
import com.example.ui.AuthState
import com.example.ui.SapanaParkViewModel

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val routeIndex: Int
)

val navItems = listOf(
    NavItem("Home", Icons.Default.Home, 0),
    NavItem("Bills", Icons.Default.ReceiptLong, 1),
    NavItem("Complaints", Icons.Default.Build, 2),
    NavItem("Events", Icons.Default.Event, 3),
    NavItem("Profile", Icons.Default.Person, 4)
)

@Composable
fun AppNavigation(
    viewModel: SapanaParkViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsState()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    if (authState !is AuthState.LoggedIn) {
        AuthScreen(viewModel = viewModel, modifier = modifier)
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation_bar"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = selectedTabIndex == item.routeIndex,
                            onClick = { selectedTabIndex = item.routeIndex },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            modifier = Modifier.testTag("nav_item_${item.title.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            val screenModifier = Modifier.padding(innerPadding)
            when (selectedTabIndex) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToTab = { selectedTabIndex = it },
                    modifier = screenModifier
                )
                1 -> FinanceScreen(
                    viewModel = viewModel,
                    modifier = screenModifier
                )
                2 -> ComplaintsScreen(
                    viewModel = viewModel,
                    modifier = screenModifier
                )
                3 -> CommunityScreen(
                    viewModel = viewModel,
                    modifier = screenModifier
                )
                4 -> ProfileScreen(
                    viewModel = viewModel,
                    modifier = screenModifier
                )
            }
        }
    }
}
