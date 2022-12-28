package com.sep.gothere.data

import com.sep.gothere.api.GoThereApi
import com.sep.gothere.api.model.common.response.ApiResponse
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.api.model.venue.get.response.GetVenueResponse
import com.sep.gothere.api.model.venue.put.request.PutVenueRequest
import com.sep.gothere.api.model.venue.put.response.PutVenueResponse
import com.sep.gothere.current.ACCESS_TOKEN
import com.sep.gothere.practices.networkOnlyImmediateResource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NetworkVenueRepository @Inject constructor(
    private val goThereApi: GoThereApi,
) {
    suspend fun getVenueRP(
        getVenueRequest: GetVenueRequest,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (GetVenueResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.getVenue(
                "Bearer $ACCESS_TOKEN",
                getVenueRequest.venueId,
                getVenueRequest.search,
                getVenueRequest.random,
                getVenueRequest.take,
                getVenueRequest.skip,
                getVenueRequest.order
            )
        },
        onFetchLoading = onFetchLoading,
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = { t ->
            if (t !is HttpException && t !is IOException) {
                throw t
            }
            onFetchFailed(t)
        }
    )

    suspend fun registerVenueRP(
        putVenueRequest: PutVenueRequest,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (PutVenueResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.registerVenue("Bearer $ACCESS_TOKEN", putVenueRequest)
        },
        onFetchLoading = onFetchLoading,
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = { t ->
            if (t !is HttpException && t !is IOException) {
                throw t
            }
            onFetchFailed(t)
        }
    )

    suspend fun updateVenueRP(
        putVenueRequest: PutVenueRequest,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (PutVenueResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.updateVenue("Bearer $ACCESS_TOKEN", putVenueRequest)
        },
        onFetchLoading = onFetchLoading,
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = { t ->
            if (t !is HttpException && t !is IOException) {
                throw t
            }
            onFetchFailed(t)
        }
    )

    suspend fun venueUsernameCheckRP(
        venueUsernameCheckRequest: String,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (ApiResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.checkIfVenueUsernameIsApplicable(venueUsernameCheckRequest)
        },
        onFetchLoading = onFetchLoading,
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = { t ->
            if (t !is HttpException && t !is IOException) {
                throw t
            }
            onFetchFailed(t)
        }
    )

    suspend fun getUserVenueRP(
        getVenueRequest: GetVenueRequest,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (GetVenueResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.getUserVenue(
                "Bearer $ACCESS_TOKEN",
                getVenueRequest.venueId,
                getVenueRequest.search,
                getVenueRequest.random,
                getVenueRequest.take,
                getVenueRequest.skip,
                getVenueRequest.order
            )
        },
        onFetchLoading = onFetchLoading,
        onFetchSuccess = onFetchSuccess,
        onFetchFailed = { t ->
            if (t !is HttpException && t !is IOException) {
                throw t
            }
            onFetchFailed(t)
        }
    )

    suspend fun getVenueProfileImageRP(venueID: Long) =
        goThereApi.getVenueProfileImage("Bearer $ACCESS_TOKEN", venueID)

    suspend fun getVenueCoverImageRP(venueID: Long) =
        goThereApi.getVenueCoverImage("Bearer $ACCESS_TOKEN", venueID)
}