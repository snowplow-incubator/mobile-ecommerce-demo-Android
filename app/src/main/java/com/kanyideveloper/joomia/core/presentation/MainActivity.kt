package com.kanyideveloper.joomia.core.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kanyideveloper.joomia.NavGraphs
import com.kanyideveloper.joomia.core.presentation.components.CustomScaffold
import com.kanyideveloper.joomia.core.presentation.ui.theme.JoomiaTheme
import com.kanyideveloper.joomia.destinations.AccountScreenDestination
import com.kanyideveloper.joomia.destinations.CartScreenDestination
import com.kanyideveloper.joomia.destinations.HomeScreenDestination
import com.kanyideveloper.joomia.destinations.WishlistScreenDestination
import com.kanyideveloper.joomia.tracking.SnowplowTracker
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analytics: SnowplowTracker
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            JoomiaTheme {
                ComposeDemoApp(analytics)
            }
        }
    }
}

@Composable
fun ComposeDemoApp(analytics: SnowplowTracker) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        val navController = rememberNavController()
        val navHostEngine = rememberNavHostEngine()
        val newBackStackEntry by navController.currentBackStackEntryAsState()
        val route = newBackStackEntry?.destination?.route

//        analytics.AutoTrackScreenView(navController = navController)
        analytics.Track()

        CustomScaffold(
            navController = navController,
            showBottomBar = route in listOf(
                HomeScreenDestination.route,
                WishlistScreenDestination.route,
                CartScreenDestination.route,
                AccountScreenDestination.route
            )
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                    navController = navController,
                    engine = navHostEngine
                )
            }
        }
    }
}
