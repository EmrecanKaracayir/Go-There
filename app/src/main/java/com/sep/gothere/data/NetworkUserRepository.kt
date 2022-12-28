package com.sep.gothere.data

import com.sep.gothere.api.GoThereApi
import com.sep.gothere.api.model.common.response.ApiResponse
import com.sep.gothere.api.model.login.request.LoginRequest
import com.sep.gothere.api.model.user.register.request.UserRegisterRequest
import com.sep.gothere.api.model.login.response.LoginResponse
import com.sep.gothere.api.model.me.response.MeResponse
import com.sep.gothere.api.model.user.register.response.UserRegisterResponse
import com.sep.gothere.practices.networkOnlyImmediateResource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(
    private val goThereApi: GoThereApi,
) {

    suspend fun meRP(
        token: String,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (MeResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.me("Bearer $token")
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

    suspend fun userLoginRP(
        loginRequest: LoginRequest,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (LoginResponse) -> Unit,
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
        userRegisterRequest: UserRegisterRequest,
        onFetchLoading: () -> Unit,
        onFetchSuccess: (UserRegisterResponse) -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ) = networkOnlyImmediateResource(
        fetch = {
            goThereApi.userRegister(userRegisterRequest)
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