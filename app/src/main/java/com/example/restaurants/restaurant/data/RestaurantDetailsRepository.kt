package com.example.restaurants.restaurant.data

import com.example.restaurants.restaurant.data.remote.RestaurantsApiService
import com.example.restaurants.restaurant.domain.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantDetailsRepository @Inject constructor(
    private val restInterface: RestaurantsApiService
){
//    private var restInterface: RestaurantsApiService = Retrofit.Builder()
//        .addConverterFactory(GsonConverterFactory.create())
//        .baseUrl("https://restaraunt-462ea-default-rtdb.firebaseio.com/")
//        .build()
//        .create(RestaurantsApiService::class.java)

    suspend fun getRemoteRestaurant(id: Int): Restaurant {
        return withContext(Dispatchers.IO){
            val responseMap = restInterface.getRestaurant(id)
            return@withContext responseMap.values.first().let {
                Restaurant(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    isFavorite = false
                )
            }
        }
    }
}