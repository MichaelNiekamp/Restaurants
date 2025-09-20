package com.example.restaurants.restaurant.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RestaurantsDaoInterface {
    @Query("SELECT * FROM restaurants")
    suspend fun getAllRestaurants(): List<LocalRestaurant>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun addAll(restaurants: List<LocalRestaurant>)

    @Update(entity = LocalRestaurant::class)
    suspend fun updateRestaurant(partialRestaurant: PartialLocalRestaurant)

    @Update(entity = LocalRestaurant::class)
    suspend fun updateAll(partialRestaurants: List<PartialLocalRestaurant>)

    @Query("SELECT * FROM restaurants WHERE is_favorite = 1")
    suspend fun getAllFavoriteRestaurants(): List<LocalRestaurant>
}