package com.jetbrains.kmpapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jetbrains.kmpapp.screens.DetailScreen
import com.jetbrains.kmpapp.screens.ListScreen
import kotlinx.serialization.Serializable

/**
 * Navigation route definition for the List Screen.
 * Using kotlinx.serialization allows for type-safe Compose Navigation.
 */
@Serializable
object ListDestination

/**
 * Navigation route definition for the Detail Screen.
 * @property objectId The ID of the museum object to display.
 */
@Serializable
data class DetailDestination(val objectId: Int)

/**
 * The root Composable of the Android app.
 * Configures the Material theme and sets up the navigation graph.
 */
@Composable
fun App() {
    // Dynamically apply a dark or light Material theme based on system settings
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        // Surface provides a background color and text color setup
        Surface {
            // Remember the navigation controller to manage backstack
            val navController = rememberNavController()
            
            // Define the navigation graph with type-safe routes
            NavHost(navController = navController, startDestination = ListDestination) {
                // Route: List Screen
                composable<ListDestination> {
                    ListScreen(navigateToDetails = { objectId ->
                        // Navigate to the detail screen passing the object ID argument
                        navController.navigate(DetailDestination(objectId))
                    })
                }
                
                // Route: Detail Screen
                composable<DetailDestination> { backStackEntry ->
                    // Extract the type-safe argument from the route
                    DetailScreen(
                        objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                        navigateBack = {
                            // Pop the backstack to return to the list
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
