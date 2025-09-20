package com.example.restaurants.restaurant.domain

import com.example.restaurants.restaurant.data.RestaurantsRepository

class GetSortedRestaurantsUseCase {
    private val repository: RestaurantsRepository = RestaurantsRepository()

    suspend operator fun invoke(): List<Restaurant> {
        return repository.getRestaurants().sortedBy {
            it.title
        }
    }
}