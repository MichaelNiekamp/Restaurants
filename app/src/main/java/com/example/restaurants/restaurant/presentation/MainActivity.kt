package com.example.restaurants.restaurant.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.restaurants.restaurant.presentation.details.RestaurantDetailsScreen
import com.example.restaurants.restaurant.presentation.list.RestaurantsScreen
import com.example.restaurants.restaurant.presentation.list.RestaurantsViewModel
import com.example.restaurants.ui.theme.RestaurantsTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContent {
            RestaurantsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RestaurantApp()
                }
            }
        }
    }
}


@Composable
private fun RestaurantApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "restaurants") {
        composable("restaurants") {
            val viewModel: RestaurantsViewModel = hiltViewModel()
            RestaurantsScreen(
                state = viewModel.state.value,
                onItemClick = { id ->
                    Timber.tag("Navigation").d("Navigating to details of restaurant with id $id")
                    navController.navigate("restaurants/$id")
                },
                onToggleFavorite = { id, oldValue ->
                    viewModel.toggleFavorite(id, oldValue)
                }
            )
        }
        composable(
            route = "restaurants/{restaurant_id}",
            arguments = listOf(
                navArgument(
                    "restaurant_id"
                ) {
                    type = NavType.IntType
                })
        ) { RestaurantDetailsScreen() }
    }
}
