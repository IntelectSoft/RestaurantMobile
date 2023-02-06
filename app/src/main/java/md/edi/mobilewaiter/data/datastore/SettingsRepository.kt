package md.edi.mobilewaiter.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


private val Context.settings: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository @Inject constructor(private val context: Context) : DataStoreProvider() {

    override fun provideDataStore(): DataStore<Preferences> {
        return context.settings
    }

    //device settings

    fun getFirebaseToken(): Flow<String?> {
        return getFlow {
            val key = stringPreferencesKey(TKN)
            it[key]
        }
    }

    suspend fun setFirebaseToken(value: String) {
        edit {
            val key = stringPreferencesKey(TKN)
            it[key] = value
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


    //license settings
    fun getLicenseCode(): Flow<String> {
        return getFlow {
            val key = stringPreferencesKey(LIC_CODE)
            it[key] ?: ""
        }
    }

    suspend fun setLicenseCode(value: String) {
        edit {
            val key = stringPreferencesKey(LIC_CODE)
            it[key] = value
        }
    }

    fun getLicenseId(): Flow<String> {
        return getFlow {
            val key = stringPreferencesKey(LIC_ID)
            it[key] ?: ""
        }
    }

    suspend fun setLicenseId(value: String) {
        edit {
            val key = stringPreferencesKey(LIC_ID)
            it[key] = value
        }
    }

    fun getURI(): Flow<String> {
        return getFlow {
            val key = stringPreferencesKey(URI)
            it[key] ?: ""
        }
    }

    suspend fun setURI(value: String) {
        edit {
            val key = stringPreferencesKey(URI)
            it[key] = value
        }
    }

    fun getCompany(): Flow<String> {
        return getFlow {
            val key = stringPreferencesKey(COMPANY)
            it[key] ?: ""
        }
    }

    suspend fun setCompany(value: String) {
        edit {
            val key = stringPreferencesKey(COMPANY)
            it[key] = value
        }
    }

    fun getSrvDate(): Flow<Long> {
        return getFlow {
            val key = longPreferencesKey(SRV_DATE)
            it[key] ?: 0L
        }
    }

    suspend fun setSrvDate(value: Long) {
        edit {
            val key = longPreferencesKey(SRV_DATE)
            it[key] = value
        }
    }

    fun getAppDate(): Flow<Long> {
        return getFlow {
            val key = longPreferencesKey(APP_DATE)
            it[key] ?: 0L
        }
    }

    suspend fun setAppDate(value: Long) {
        edit {
            val key = longPreferencesKey(APP_DATE)
            it[key] = value
        }
    }

    fun getPositionX(): Flow<Float> {
        return getFlow {
            val key = floatPreferencesKey(X_POSITION)
            it[key] ?: 0f
        }
    }

    fun getPositionY(): Flow<Float> {
        return getFlow {
            val key = floatPreferencesKey(Y_POSITION)
            it[key] ?: 0f
        }
    }

    suspend fun setPositionX(value: Float) {
        edit {
            val key = floatPreferencesKey(X_POSITION)
            it[key] = value
        }
    }

    suspend fun setPositionY(value: Float) {
        edit {
            val key = floatPreferencesKey(Y_POSITION)
            it[key] = value
        }
    }


    private companion object {
        //KEYS
        const val X_POSITION = "x"
        const val Y_POSITION = "y"

        const val APP_DATE = "app_date"
        const val URI = "uri"
        const val SRV_DATE = "srv_date"
        const val COMPANY = "company"
        const val LIC_ID = "lic_id"
        const val LIC_CODE = "lic_code"
        const val DEV_ID = "deviceId"
        const val TKN = "token"
    }
}