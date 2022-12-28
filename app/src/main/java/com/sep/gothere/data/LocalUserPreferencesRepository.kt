package com.sep.gothere.data

import com.sep.gothere.managers.datastore.DataStoreManager
import javax.inject.Inject

class LocalUserPreferencesRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {

    suspend fun getCachedTokenRP() =
        dataStoreManager.getCachedToken()

    suspend fun updateCachedTokenRP(cachedToken: String) =
        dataStoreManager.updateCachedToken(cachedToken)

    suspend fun deleteCachedTokenRP() =
        dataStoreManager.deleteCachedToken()
}