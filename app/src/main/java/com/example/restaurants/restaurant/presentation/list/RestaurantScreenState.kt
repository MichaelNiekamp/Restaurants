package com.example.restaurants.restaurant.presentation.list

import com.example.restaurants.restaurant.domain.Restaurant

data class RestaurantScreenState(
    val restaurants: List<Restaurant>,
    val isLoading: Boolean,
    val error: String? = null
)
