package com.example.restaurants

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RestaurantsDaoInterface {
    @Query("SELECT * FROM restaurants")
    suspend fun getAllRestaurants(): List<Restaurant>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun addAll(restaurants: List<Restaurant>)

    @Update(entity = Restaurant::class)
    suspend fun updateRestaurant(partialRestaurant: PartialRestaurant)

    @Update(entity = Restaurant::class)
    suspend fun updateAll(partialRestaurants: List<PartialRestaurant>)

    @Query("SELECT * FROM restaurants WHERE is_favorite = 1")
    suspend fun getAllFavoriteRestaurants(): List<Restaurant>
}