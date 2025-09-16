package com.example.restaurants

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

    private var restaurantsDaoInterface = RestaurantsDb.getInstance(RestaurantsApplication.getAppContext())

    suspend fun toggleFavoriteRestaurant(id: Int, oldValue: Boolean) =
        withContext(Dispatchers.IO) {
            restaurantsDaoInterface.updateRestaurant(PartialRestaurant(id, !oldValue))
            restaurantsDaoInterface.getAllRestaurants()
        }

    suspend fun getAllRestaurants(): List<Restaurant> {
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
            return@withContext restaurantsDaoInterface.getAllRestaurants()
        }
    }

    private suspend fun refreshCache() {
        val remoteRestaurants = restInterface.getRestaurants()
        val favoriteRestaurants = restaurantsDaoInterface.getAllFavoriteRestaurants()
        restaurantsDaoInterface.addAll(remoteRestaurants)
        restaurantsDaoInterface.updateAll(favoriteRestaurants.map {
            PartialRestaurant(it.id, true)
        })
    }
}