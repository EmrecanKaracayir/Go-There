package com.sep.gothere.data

import com.sep.gothere.managers.datastore.DataStoreManager
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {

    suspend fun getCachedUserRP() =
        dataStoreManager.getCachedUser()

    suspend fun updateCachedUserRP(cachedUser: CachedUser) =
        dataStoreManager.updateCachedUser(cachedUser)
}