package com.example.restaurants.restaurant.domain

import com.example.restaurants.restaurant.data.RestaurantsRepository
import javax.inject.Inject

class GetSortedRestaurantsUseCase @Inject constructor(
    private val repository: RestaurantsRepository
) {
    suspend operator fun invoke(): List<Restaurant> {
        return repository.getRestaurants().sortedBy {
            it.title
        }
    }
}