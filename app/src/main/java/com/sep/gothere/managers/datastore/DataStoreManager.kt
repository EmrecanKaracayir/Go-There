package com.sep.gothere.managers.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    @ApplicationContext val appContext: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_NAME
    )

    suspend fun getCachedToken(): String? {
        val preferences = appContext.dataStore.data
        val tokenKey = stringPreferencesKey(CACHED_TOKEN_KEY)
        return preferences.first()[tokenKey]
    }

    suspend fun updateCachedToken(cachedToken: String) =
        appContext.dataStore.edit { preferences ->
            val tokenKey = stringPreferencesKey(CACHED_TOKEN_KEY)
            preferences[tokenKey] = cachedToken
        }

    suspend fun deleteCachedToken() =
        appContext.dataStore.edit { preferences ->
            val tokenKey = stringPreferencesKey(CACHED_TOKEN_KEY)
            preferences.remove(tokenKey)
        }

    companion object {
        const val PREFERENCES_NAME = "user_preferences"
        const val CACHED_TOKEN_KEY = "cachedToken"
    }
}