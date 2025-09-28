package com.example.restaurants.restaurant.data

import com.example.restaurants.restaurant.data.local.LocalRestaurant
import com.example.restaurants.restaurant.data.local.PartialLocalRestaurant
import com.example.restaurants.restaurant.data.remote.RestaurantsApiService
import com.example.restaurants.restaurant.data.di.IODispatcher
import com.example.restaurants.restaurant.data.local.RestaurantsDaoInterface
import com.example.restaurants.restaurant.domain.Restaurant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantsRepository @Inject constructor(
    private val restInterface: RestaurantsApiService,
    private val restaurantsDaoInterface: RestaurantsDaoInterface,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun toggleFavoriteRestaurant(id: Int, value: Boolean) =
        withContext(dispatcher) {
            restaurantsDaoInterface.updateRestaurant(PartialLocalRestaurant(id, value))
        }

    suspend fun loadRestaurants() {
        Timber.tag("API").d("Fetching restaurants from API")
        return withContext(dispatcher) {
            try {
                refreshCache()
            } catch (e: Exception) {
                Timber.tag("API").e("Error fetching restaurants: ${e.message}")
                when (e) {
                    is UnknownHostException,
                    is ConnectException,
                    is HttpException -> {
                        if (restaurantsDaoInterface.getAllRestaurants().isEmpty()) {
                            throw Exception("No data available")
                        }
                    }

                    else -> throw e
                }
            }
        }
    }

    private suspend fun refreshCache() {
        val remoteRestaurants = restInterface.getRestaurants()
        val favoriteRestaurants = restaurantsDaoInterface.getAllFavoriteRestaurants()
        restaurantsDaoInterface.addAll(remoteRestaurants.map {
            LocalRestaurant(
                id = it.id,
                title = it.title,
                description = it.description,
                isFavorite = false
            )
        })
        restaurantsDaoInterface.updateAll(favoriteRestaurants.map {
            PartialLocalRestaurant(it.id, true)
        })
    }

    suspend fun getRestaurants() : List<Restaurant> {
        return withContext(dispatcher) {
            return@withContext restaurantsDaoInterface.getAllRestaurants().map {
                Restaurant(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    isFavorite = it.isFavorite
                )
            }
        }
    }
}