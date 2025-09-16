package com.example.restaurants

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class RestaurantDetailsViewModel(private val stateHandle: SavedStateHandle): ViewModel() {
    private val _state = mutableStateOf<Restaurant?>(null)
    val state: State<Restaurant?>
        get() = _state

    private val repository = RestaurantDetailsRepository()

    init {
        val id = stateHandle.get<Int>("restaurant_id") ?: 0
        viewModelScope.launch {
            Timber.tag("API").d("Fetching restaurant with id $id")
            getRestaurant(id)
        }
    }

    private fun getRestaurant(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    _state.value = repository.getRemoteRestaurant(id)
                } catch (e: Exception) {
                    Timber.tag("API").e("Error fetching restaurant details: ${e.message}")
                }
            }
        }
    }
}