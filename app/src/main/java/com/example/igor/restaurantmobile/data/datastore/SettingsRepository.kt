package com.example.igor.restaurantmobile.data.datastore

import android.content.Context
import androidx.annotation.IntRange
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


private val Context.settings: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository @Inject constructor(private val context: Context) : DataStoreProvider() {

    override fun provideDataStore(): DataStore<Preferences> {
        return context.settings
    }

    fun getBrightness(): Flow<Int> {
        return getFlow {
            val key = intPreferencesKey(BRIGHTNESS)
            it[key] ?: 100
        }
    }

    suspend fun setBrightness(@IntRange(from = 0, to = 100) value: Int) {
        edit {
            val key = intPreferencesKey(BRIGHTNESS)
            it[key] = value
        }
    }

//    fun getLogs(): Flow<LogsData> {
//        return getFlow {
//            val key = stringPreferencesKey(LOGS)
//            val adapter = getMoshi().adapter(LogsData::class.java)
//            it[key]?.let { data -> adapter.fromJson(data) } ?: LogsData()
//        }
//    }
//
//    suspend fun setLogs(data: LogsData) {
//        edit {
//            val key = stringPreferencesKey(LOGS)
//            val adapter = getMoshi().adapter(LogsData::class.java)
//            it[key] = adapter.toJson(data)
//        }
//    }

    suspend fun setGeofenceFreq(value: Int) {
        edit {
            val key = intPreferencesKey(GEO_FREQ)
            it[key] = value
        }
    }

    fun getGeofenceFreq(): Flow<Int> {
        return getFlow {
            val key = intPreferencesKey(GEO_FREQ)
            it[key] ?: 0
        }
    }

    fun getDeviceId(): Flow<String> {
        return getFlow {
            val key = stringPreferencesKey(DEV_ID)
            it[key] ?: ""
        }
    }

    suspend fun setDeviceId(value: String) {
        edit {
            val key = stringPreferencesKey(DEV_ID)
            it[key] = value
        }
    }

    fun getLicense(): Flow<String> {
        return getFlow {
            val key = stringPreferencesKey(DEV_ID)
            it[key] ?: ""
        }
    }

    suspend fun setLicense(value: String) {
        edit {
            val key = stringPreferencesKey(DEV_ID)
            it[key] = value
        }
    }


    private companion object {
        //KEYS
        const val BRIGHTNESS = "brightness"
        const val PLAYER = "player"
        const val LOGS = "logs"
        const val FILLERS = "fillers"
        const val GEO_FREQ = "geoFreq"
        const val FIL_FREQ = "filFreq"
        const val URLS = "urls"
        const val PWRD = "password"
        const val DEV_ID = "deviceId"
        const val TKN = "token"
    }
}