package com.example.restaurants

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Restaurant::class],
    version = 2,
    exportSchema = false
)
abstract class RestaurantsDb : RoomDatabase() {
    abstract val restaurantsDao: RestaurantsDaoInterface

    companion object {
        @Volatile
        private var INSTANCE: RestaurantsDaoInterface? = null

        fun getInstance(context: Context): RestaurantsDaoInterface {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = buildDatabase(context).restaurantsDao
                    INSTANCE = instance
                }
                return instance
            }
        }

        private fun buildDatabase(context: Context): RestaurantsDb =
            Room.databaseBuilder(
                context.applicationContext,
                RestaurantsDb::class.java,
                "restaurants_database"
            )
                .fallbackToDestructiveMigration(false)
                .build()
    }
}