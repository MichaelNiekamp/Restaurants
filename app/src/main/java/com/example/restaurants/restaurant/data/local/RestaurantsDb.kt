package com.example.restaurants.restaurant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LocalRestaurant::class],
    version = 3,
    exportSchema = false
)
abstract class RestaurantsDb : RoomDatabase() {
    abstract val restaurantsDao: RestaurantsDaoInterface
}