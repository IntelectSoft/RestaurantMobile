package com.example.igor.restaurantmobile.presentation.launch.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.igor.restaurantmobile.data.remote.response.assortment.AssortmentListResponse
import com.example.igor.restaurantmobile.data.remote.response.terminal.RegisterTerminalResponse
import com.example.igor.restaurantmobile.data.repo.RepositoryServiceImpl
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val serviceRepo: RepositoryServiceImpl,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val terminalRegister = MutableSharedFlow<RegisterTerminalResponse>()
    val syncAssortment = MutableSharedFlow<AssortmentListResponse>()

    fun registerTerminal() {
        viewModelScope.launch {
            val deviceId = settingsRepository.getDeviceId().first()

            try {
                val response = serviceRepo.registerTerminal(deviceId)
                Log.e("TAG", "response: $response")
                if (response.isSuccessful) {
                    response.body()?.let { terminalRegister.emit(it) }
                }
                else{
                    response.errorBody()
                }
            }
            catch (ex: Exception){
                terminalRegister.emit(RegisterTerminalResponse(ResultMessage = ex.message))
            }
        }
    }

    fun syncAssortment() {
        viewModelScope.launch {
            val deviceId = settingsRepository.getDeviceId().first()
            val response = serviceRepo.syncAssortment(deviceId, false)
            if(response.isSuccessful){
                response.body()?.let { syncAssortment.emit(it) }
            }
        }
    }

}