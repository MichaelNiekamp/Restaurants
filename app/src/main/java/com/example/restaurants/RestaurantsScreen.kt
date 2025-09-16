package com.example.restaurants

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun RestaurantsScreen(onItemClick: (id: Int) -> Unit) {
    val viewModel: RestaurantsViewModel = viewModel()
    val state = viewModel.state.value
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)){
            item() {
                Text(text = "Restaurant Guide", style = TextStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 24.sp), modifier = Modifier.padding(8.dp))
            }
            items(state.restaurants.size) { restaurant ->
                RestaurantItem(
                    state.restaurants[restaurant],
                    onClick = { id, oldValue ->
                        viewModel.toggleFavorite(id, oldValue)
                    },
                    onItemClick = { id ->
                        onItemClick(id)
                    })
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        if (state.error != null) {
            Text(text = state.error, color = MaterialTheme.colorScheme.error)
        }
    }
}


@Composable
fun RestaurantItem(item: Restaurant, onClick: (id: Int, oldValue: Boolean) -> Unit, onItemClick: (id: Int)-> Unit) {
    val favoriteState = remember { mutableStateOf(false) }
    val favoriteIcon = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder

    Card(elevation = cardElevation(defaultElevation = 4.dp), modifier = Modifier.padding(8.dp).clickable { onItemClick(item.id) }) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            RestaurantIcon(Icons.Filled.Place, Modifier.weight(0.15f), onClick = { })
            RestaurantDetails(item.title, item.description, Modifier.weight(0.7f))
            RestaurantIcon(favoriteIcon, Modifier.weight(0.15f), onClick = { onClick(item.id, item.isFavorite) })
        }
    }
}

@Composable
fun RestaurantIcon(icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Image(imageVector = icon, contentDescription = "Favorite icon", modifier = modifier
        .padding(8.dp)
        .clickable { onClick() })
}



@Composable
fun RestaurantDetails(title: String, description: String, modifier: Modifier, horizontalAlignment: Alignment.Horizontal = Alignment.Start) {
    Column (modifier = modifier, horizontalAlignment = horizontalAlignment) {
        // Create a text composable with bold style
        Text(text = title, style = TextStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, fontSize = 18.sp))
        // Create a text composable with smaller text than the line above and grey text color
        Text(text = description, style = TextStyle(fontSize = 14.sp, color = Color.Gray))
    }
}