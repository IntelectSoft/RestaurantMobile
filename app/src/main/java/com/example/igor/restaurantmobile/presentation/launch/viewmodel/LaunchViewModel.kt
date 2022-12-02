package com.example.igor.restaurantmobile.presentation.launch.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.igor.restaurantmobile.data.remote.response.assortment.AssortmentListResponse
import com.example.igor.restaurantmobile.data.remote.response.terminal.RegisterTerminalResponse
import com.example.igor.restaurantmobile.data.repo.RepositoryServiceImpl
import com.example.igor.restaurantmobile.data.datastore.SettingsRepository
import com.example.igor.restaurantmobile.data.remote.models.RegisterDeviceModel
import com.example.igor.restaurantmobile.data.remote.models.license.GetUriModel
import com.example.igor.restaurantmobile.data.remote.models.license.RegisterModel
import com.example.igor.restaurantmobile.data.remote.models.license.response.RegisterResponse
import com.example.igor.restaurantmobile.utils.DeviceInfo
import com.example.igor.restaurantmobile.utils.Urls
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
    val registerApplication = MutableSharedFlow<RegisterResponse>()
    val getUri = MutableSharedFlow<RegisterResponse>()

    fun registerTerminal() {
        viewModelScope.launch {
            try {
                val response = serviceRepo.registerTerminal(Urls.RegisterDevice, RegisterDeviceModel(DeviceInfo.deviceId, DeviceInfo.firebaseId) )
                if (response.isSuccessful) {
                    response.body()?.let { terminalRegister.emit(it) }
                } else {
                    terminalRegister.emit(RegisterTerminalResponse(ResultMessage =  response.errorBody()?.string()))
                }
            } catch (ex: Exception) {
                terminalRegister.emit(RegisterTerminalResponse(ResultMessage = ex.message))
            }
        }
    }

    fun syncAssortment() {
        viewModelScope.launch {
            try {
                val response = serviceRepo.syncAssortment(Urls.GetAssortimentList, DeviceInfo.deviceId, false)
                if (response.isSuccessful) {
                    response.body()?.let { syncAssortment.emit(it) }
                }
                else{
                    syncAssortment.emit(AssortmentListResponse(Result = -9, errorMessage = response.errorBody()?.string() ))
                }
            }
            catch (e: Exception){
                syncAssortment.emit(AssortmentListResponse(Result = -9, errorMessage = e.message))
            }
        }
    }

    fun registerApplication(code: String) {
        viewModelScope.launch {
            val registerModel = RegisterModel(
                ApplicationVersion = DeviceInfo.appVersion,
                LicenseActivationCode = code,
                DeviceID = DeviceInfo.deviceId,
                DeviceModel = DeviceInfo.deviceModel,
                DeviceName = DeviceInfo.deviceName,
                OSType = DeviceInfo.osType,
                OSVersion = DeviceInfo.osVersion,
                ProductType = DeviceInfo.productType
            )
            try {
                val response = serviceRepo.registerApplication(Urls.RegisterUrl, registerModel)
                if (response.isSuccessful) {
                    response.body()?.let { registerApplication.emit(it) }
                }
                else{
                    registerApplication.emit(RegisterResponse(ErrorMessage = response.errorBody()?.string(), ErrorCode = -9))
                }
            } catch (e: Exception) {
                registerApplication.emit(RegisterResponse(ErrorMessage = e.message, ErrorCode = -9))
            }
        }
    }

    fun getURI() {
        viewModelScope.launch {
            val getUriModel = GetUriModel(
                ApplicationVersion = DeviceInfo.appVersion,
                DeviceID = DeviceInfo.deviceId,
                LicenseID = DeviceInfo.licenseId,
                LicenseActivationCode = DeviceInfo.licenseCode,
                DeviceName = DeviceInfo.deviceName,
                DeviceModel = DeviceInfo.deviceModel,
                OSVersion = DeviceInfo.osVersion,
                OSType = DeviceInfo.osType,
                ProductType = DeviceInfo.productType,
            )
            try {
                val response = serviceRepo.getUri(Urls.GetUri, getUriModel)
                if (response.isSuccessful) {
                    response.body()?.let {
                        getUri.emit(it)
                    }
                }
                else{
                    getUri.emit(RegisterResponse(ErrorMessage = response.errorBody()?.string(), ErrorCode = -9))
                }
            } catch (e: Exception) {
                getUri.emit(RegisterResponse(ErrorMessage = e.message, ErrorCode = -9))
            }
        }
    }
}