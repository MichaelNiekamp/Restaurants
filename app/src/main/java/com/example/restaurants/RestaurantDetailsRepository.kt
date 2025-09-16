package com.example.restaurants

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestaurantDetailsRepository {
    private var restInterface: RestaurantsApiService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://restaraunt-462ea-default-rtdb.firebaseio.com/")
        .build()
        .create(RestaurantsApiService::class.java)

    suspend fun getRemoteRestaurant(id: Int): Restaurant {
        return withContext(Dispatchers.IO){
            val responseMap = restInterface.getRestaurant(id)
            return@withContext responseMap.values.first()
        }
    }
}