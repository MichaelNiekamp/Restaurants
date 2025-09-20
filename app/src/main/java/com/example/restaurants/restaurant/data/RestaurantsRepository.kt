package com.example.restaurants.restaurant.data

import com.example.restaurants.restaurant.data.local.LocalRestaurant
import com.example.restaurants.restaurant.data.local.PartialLocalRestaurant
import com.example.restaurants.restaurant.data.remote.RestaurantsApiService
import com.example.restaurants.RestaurantsApplication
import com.example.restaurants.restaurant.data.local.RestaurantsDb
import com.example.restaurants.restaurant.domain.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.net.ConnectException
import java.net.UnknownHostException

class RestaurantsRepository {
    private var restInterface: RestaurantsApiService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://restaraunt-462ea-default-rtdb.firebaseio.com/")
        .build()
        .create(RestaurantsApiService::class.java)

    private var restaurantsDaoInterface =
        RestaurantsDb.getInstance(RestaurantsApplication.getAppContext())

    suspend fun toggleFavoriteRestaurant(id: Int, value: Boolean) =
        withContext(Dispatchers.IO) {
            restaurantsDaoInterface.updateRestaurant(PartialLocalRestaurant(id, value))
        }

    suspend fun loadRestaurants() {
        Timber.tag("API").d("Fetching restaurants from API")
        return withContext(Dispatchers.IO) {
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
        return withContext(Dispatchers.IO) {
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