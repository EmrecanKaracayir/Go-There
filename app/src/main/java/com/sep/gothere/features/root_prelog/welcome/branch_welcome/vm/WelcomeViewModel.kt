package com.sep.gothere.features.root_prelog.welcome.branch_welcome.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sep.gothere.api.model.login.request.LoginRequest
import com.sep.gothere.api.model.login.response.LoginResponse
import com.sep.gothere.data.NetworkUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val networkUserRepository: NetworkUserRepository
) : ViewModel() {
    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private suspend fun userLoginVM(
        loginRequest: LoginRequest
    ) = networkUserRepository.userLoginRP(
        loginRequest,
        onFetchLoading = {
            viewModelScope.launch {
                eventChannel.send(
                    Event.LoginLoading(null)
                )
            }
        },
        onFetchSuccess = {
            viewModelScope.launch {
                eventChannel.send(
                    Event.LoginSuccessful(it)
                )
            }
        },
        onFetchFailed = { throwable ->
            viewModelScope.launch {
                eventChannel.send(
                    Event.LoginError(throwable)
                )
            }
        }
    )

    suspend fun loginButtonClicked(username: String, password: String) {
        userLoginVM(LoginRequest(username, password))
    }

    private val _username = MutableStateFlow("")
    fun setUsername(username: String) {
        _username.value = username
    }

    private val _password = MutableStateFlow("")
    fun setPassword(password: String) {
        _password.value = password
    }


    val isLoginButtonEnabled: Flow<Boolean> =
        combine(_username, _password) { username, password ->
            val isUsernameFilled = username.isNotBlank()
            val isPasswordFilled = password.isNotBlank()
            return@combine isUsernameFilled and isPasswordFilled
        }

    sealed class Event {
        data class LoginLoading(val fetchLoading: LoginResponse?) : Event()

        data class LoginSuccessful(val response: LoginResponse) : Event()

        data class LoginError(val error: Throwable) : Event()
    }
}