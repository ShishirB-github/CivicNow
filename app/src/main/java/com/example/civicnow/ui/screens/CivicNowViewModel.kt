package com.example.civicnow.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicnow.BuildConfig
import com.example.civicnow.network.Election
import com.example.civicnow.network.ElectionsApi
import com.example.civicnow.network.EventData
import com.example.civicnow.network.EventsApi
import com.example.civicnow.network.GeocodeApi
import com.example.civicnow.network.Officeholder
import com.example.civicnow.network.PeoplesApi
import java.io.IOException
import kotlinx.coroutines.launch

sealed interface CivicNowUiState {
    data class Success(val elections: List<Election>, val officeholders: List<Officeholder>, val events: List<EventData>) : CivicNowUiState
    object Error : CivicNowUiState
    object Loading : CivicNowUiState
}

class CivicNowViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var civicNowUiState: CivicNowUiState by mutableStateOf(CivicNowUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        fetchData()
    }

    private fun fetchData() {
        try {
            viewModelScope.launch {
                val electionsResponse =
                    ElectionsApi.retrofitService.getElections(BuildConfig.ELECTIONS_API_KEY)

                val latLongResult = GeocodeApi.retrofitService.getLatLong(
                    address = "94568 United States",
                    apiKey = BuildConfig.GEOCODE_API_KEY
                )

                val peoplesResponse = PeoplesApi.retrofitService.getPeoples(
                    lat = latLongResult.items[0].position.lat.toString(),
                    long = latLongResult.items[0].position.lng.toString())

                val eventsResponse = EventsApi.retrofitService.getEvents("Nevada")

                civicNowUiState = CivicNowUiState.Success(electionsResponse.elections, peoplesResponse.results, eventsResponse.results)
            }
        } catch (e: IOException) {
            civicNowUiState = CivicNowUiState.Error
        }
    }

    fun fetchOfficeHoldersForZip(zipCode: String) {
        try {
            viewModelScope.launch {
                val latLongResult = GeocodeApi.retrofitService.getLatLong(
                    address = zipCode + " United States",
                    apiKey = BuildConfig.GEOCODE_API_KEY
                )

                val peoplesResponse = PeoplesApi.retrofitService.getPeoples(
                    lat = latLongResult.items[0].position.lat.toString(),
                    long = latLongResult.items[0].position.lng.toString()
                )

                civicNowUiState = CivicNowUiState.Success(emptyList(), peoplesResponse.results, emptyList())
            }
        } catch (e: IOException) {
            civicNowUiState = CivicNowUiState.Error
        }
    }
}