package com.example.civicnow.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.civicnow.BuildConfig
import com.example.civicnow.network.Election
import com.example.civicnow.network.ElectionsApi
import com.example.civicnow.network.Officeholder
import com.example.civicnow.network.PeoplesApi
import java.io.IOException
import kotlinx.coroutines.launch

sealed interface CivicNowUiState {
    data class Success(val elections: List<Election>, val officeholders: List<Officeholder>) : CivicNowUiState
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

                val peoplesResponse = PeoplesApi.retrofitService.getPeoples(
                    lat = "37.70423",
                    long = "-121.91635")

                civicNowUiState = CivicNowUiState.Success(electionsResponse.elections, peoplesResponse.results)
            }
        } catch (e: IOException) {
            civicNowUiState = CivicNowUiState.Error
        }
    }
}