package com.sep.gothere.data

import com.sep.gothere.api.GoThereApi
import com.sep.gothere.api.model.request.LoginRequest
import com.sep.gothere.api.model.request.SignUpRequest
import com.sep.gothere.api.model.response.ApiResponse
import com.sep.gothere.practices.networkOnlyImmediateResource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val goThereApi: GoThereApi,
) {

    suspend fun userLoginRP(
        loginRequest: LoginRequest,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (ApiResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.userLogin(loginRequest)
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

    suspend fun usernameCheckRP(
        usernameCheckRequest: String,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (ApiResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.checkIfUsernameIsApplicable(usernameCheckRequest)
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

    suspend fun emailCheckRP(
        emailCheckRequest: String,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (ApiResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.checkIfEmailIsApplicable(emailCheckRequest)
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

    suspend fun userRegisterRP(
        signUpRequest: SignUpRequest,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (ApiResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.userRegister(signUpRequest)
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
}