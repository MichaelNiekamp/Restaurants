package com.example.restaurants.restaurant.presentation.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurants.restaurant.data.di.MainDispatcher
import com.example.restaurants.restaurant.domain.GetInitialRestaurantsUseCase
import com.example.restaurants.restaurant.domain.ToggleRestaurantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val getRestaurantsUseCase: GetInitialRestaurantsUseCase,
    private val toggleRestaurantUseCase: ToggleRestaurantUseCase,
    @MainDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _state = mutableStateOf(
        RestaurantScreenState(
        restaurants = listOf(),
        isLoading = true
    )
    )
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
        viewModelScope.launch(handler + dispatcher) {
            val restaurants = getRestaurantsUseCase()
            _state.value = _state.value.copy(
                restaurants = restaurants,
                isLoading = false
            )
        }
    }

    fun toggleFavorite(id: Int, oldValue: Boolean) {
        viewModelScope.launch(handler + dispatcher) {
            val updatedRestaurants = toggleRestaurantUseCase(id, oldValue)
            _state.value = _state.value.copy(
                restaurants = updatedRestaurants
            )
        }
    }
}