package com.sep.gothere.managers.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.sep.gothere.data.CachedUser
import com.sep.gothere.util.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    @ApplicationContext val appContext: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_NAME
    )

    suspend fun updateCachedUser(cachedUser: CachedUser) =
        appContext.dataStore.edit { preferences ->

            val idKey = stringPreferencesKey(CACHED_USER_ID_KEY)
            preferences[idKey] = cachedUser.cachedUser_id

            val imageKey = stringPreferencesKey(CACHED_USER_IMAGE_KEY)
            if (cachedUser.cachedUser_image != null)
                preferences[imageKey] = cachedUser.cachedUser_image

            val nameKey = stringPreferencesKey(CACHED_USER_NAME_KEY)
            preferences[nameKey] = cachedUser.cachedUser_name

            val emailKey = stringPreferencesKey(CACHED_USER_EMAIL_KEY)
            preferences[emailKey] = cachedUser.cachedUser_email

            val tokenKey = stringPreferencesKey(CACHED_USER_TOKEN_KEY)
            preferences[tokenKey] = cachedUser.cachedUser_token
        }

    suspend fun getCachedUser(): CachedUser? {
        val preferences = appContext.dataStore.data

        val idKey = stringPreferencesKey(CACHED_USER_ID_KEY)
        val cachedUserId = preferences.first()[idKey]

        val imageKey = stringPreferencesKey(CACHED_USER_IMAGE_KEY)
        val cachedUserImage = preferences.first()[imageKey]

        val nameKey = stringPreferencesKey(CACHED_USER_NAME_KEY)
        val cachedUserName = preferences.first()[nameKey]

        val emailKey = stringPreferencesKey(CACHED_USER_EMAIL_KEY)
        val cachedUserEmail = preferences.first()[emailKey]

        val tokenKey = stringPreferencesKey(CACHED_USER_TOKEN_KEY)
        val cachedUserToken = preferences.first()[tokenKey]

        val cachedUser: CachedUser? =
            cachedUserId?.let { id ->
                cachedUserName?.let { name ->
                    cachedUserEmail?.let { email ->
                        cachedUserToken?.let { token ->
                            CachedUser(id, cachedUserImage, name, email, token)
                        }
                    }
                }
            }

        return cachedUser
    }
}