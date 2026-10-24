package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CheapFindsViewModel
import com.example.ui.ScreenTab
import com.example.ui.components.PromoKitDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SavedKitsScreen
import com.example.ui.screens.SellerBoostScreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OnEmeraldContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CheapFindsApp()
            }
        }
    }
}

@Composable
fun CheapFindsApp(
    viewModel: CheapFindsViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val activeDialogKit by viewModel.activeKitDialog.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect snackbar messages
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Android back button handling: Return to Home/Discover if on another tab
    if (currentTab != ScreenTab.DISCOVER) {
        BackHandler {
            viewModel.setTab(ScreenTab.DISCOVER)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == ScreenTab.DISCOVER,
                    onClick = { viewModel.setTab(ScreenTab.DISCOVER) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.DISCOVER) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Discover"
                        )
                    },
                    label = { Text("Discover", fontWeight = if (currentTab == ScreenTab.DISCOVER) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnEmeraldContainer,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_discover")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.GENERATOR,
                    onClick = { viewModel.setTab(ScreenTab.GENERATOR) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.GENERATOR) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Studio"
                        )
                    },
                    label = { Text("Studio", fontWeight = if (currentTab == ScreenTab.GENERATOR) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnEmeraldContainer,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_generator")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.SAVED_KITS,
                    onClick = { viewModel.setTab(ScreenTab.SAVED_KITS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.SAVED_KITS) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Saved"
                        )
                    },
                    label = { Text("Saved", fontWeight = if (currentTab == ScreenTab.SAVED_KITS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnEmeraldContainer,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_saved")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.ANALYTICS,
                    onClick = { viewModel.setTab(ScreenTab.ANALYTICS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.ANALYTICS) Icons.Filled.Insights else Icons.Outlined.Insights,
                            contentDescription = "Analytics"
                        )
                    },
                    label = { Text("Earnings", fontWeight = if (currentTab == ScreenTab.ANALYTICS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnEmeraldContainer,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_analytics")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.SELLER_BOOST,
                    onClick = { viewModel.setTab(ScreenTab.SELLER_BOOST) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == ScreenTab.SELLER_BOOST) Icons.Filled.RocketLaunch else Icons.Outlined.RocketLaunch,
                            contentDescription = "Boost"
                        )
                    },
                    label = { Text("Boost", fontWeight = if (currentTab == ScreenTab.SELLER_BOOST) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnEmeraldContainer,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldContainer
                    ),
                    modifier = Modifier.testTag("nav_item_boost")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.DISCOVER -> HomeScreen(viewModel = viewModel)
                ScreenTab.GENERATOR -> GeneratorScreen(viewModel = viewModel)
                ScreenTab.SAVED_KITS -> SavedKitsScreen(viewModel = viewModel)
                ScreenTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                ScreenTab.SELLER_BOOST -> SellerBoostScreen(viewModel = viewModel)
            }
        }
    }

    // Modal Promo Kit Dialog / Teleprompter
    activeDialogKit?.let { kit ->
        PromoKitDialog(
            kit = kit,
            onDismiss = viewModel::closeKitDetail,
            onToggleFavorite = viewModel::toggleFavoriteKit,
            onLinkClickRecorded = viewModel::recordClick,
            onNotify = viewModel::emitMessage
        )
    }
}
