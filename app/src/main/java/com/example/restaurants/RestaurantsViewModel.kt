package com.example.restaurants

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.net.ConnectException
import java.net.UnknownHostException


class RestaurantsViewModel() : ViewModel() {
    private val repository = RestaurantsRepository()

    private val _state = mutableStateOf(RestaurantScreenState(
        restaurants = listOf(),
        isLoading = true
    ))
    val state: State<RestaurantScreenState>
        get() = _state

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents

    private val handler = CoroutineExceptionHandler { _, exception ->
        Timber.tag("Handler").e("Caught ${exception.printStackTrace()}")
        _state.value = _state.value.copy(
            isLoading = false,
            error = "Error: ${exception.message}"
        )
        viewModelScope.launch {
            _errorEvents.emit("Error fetching restaurants: ${exception.message}")
        }
    }

    init {
        getRestaurants()
    }

    private fun getRestaurants() {
        viewModelScope.launch(handler) {
            val restaurants = repository.getAllRestaurants()
            _state.value = _state.value.copy(
                restaurants = restaurants,
                isLoading = false
            )
        }
    }

    fun toggleFavorite(id: Int, oldValue: Boolean) {
        viewModelScope.launch(handler) {
            val updatedRestaurants = repository.toggleFavoriteRestaurant(id, oldValue)
            _state.value = _state.value.copy(
                restaurants = updatedRestaurants
            )
        }
    }
}