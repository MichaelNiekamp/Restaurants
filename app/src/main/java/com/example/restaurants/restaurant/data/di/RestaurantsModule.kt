package com.example.restaurants.restaurant.data.di

import android.content.Context
import androidx.room.Room
import com.example.restaurants.restaurant.data.local.RestaurantsDaoInterface
import com.example.restaurants.restaurant.data.local.RestaurantsDb
import com.example.restaurants.restaurant.data.remote.RestaurantsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestaurantsModule {
    @Provides
    fun provideRestaurantsDao(db: RestaurantsDb) : RestaurantsDaoInterface {
        return db.restaurantsDao
    }

    @Singleton
    @Provides
    fun provideRoomDB(
        @ApplicationContext appContext: Context
    ) : RestaurantsDb {
        return Room.databaseBuilder(
            appContext,
            RestaurantsDb::class.java,
            "restaurants_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://restaraunt-462ea-default-rtdb.firebaseio.com/")
            .build()
    }

    @Provides
    fun provideRetrofitApi(retrofit: Retrofit) : RestaurantsApiService {
        return retrofit.create(RestaurantsApiService::class.java)
    }
}