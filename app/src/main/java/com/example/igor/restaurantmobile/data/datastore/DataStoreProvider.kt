package com.example.igor.restaurantmobile.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


abstract class DataStoreProvider {
    protected abstract fun provideDataStore(): DataStore<Preferences>

    open suspend fun edit(edit: suspend (MutablePreferences) -> Unit){
        provideDataStore().edit(edit)
    }

    open suspend fun <T> get(action: suspend (Preferences) -> T): T {
        return provideDataStore().data.map(action).first()
    }

    open fun <T> getFlow(action: suspend (Preferences) -> T): Flow<T> {
        return provideDataStore().data.map(action)
    }

    open suspend fun clear(edit: suspend (MutablePreferences) -> Unit){
        provideDataStore().edit(edit)
    }
}