package com.sep.gothere.features.root_prelog.welcome.leaf_signup.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sep.gothere.api.model.user_register.request.UserRegisterRequest
import com.sep.gothere.api.model.login.response.LoginResponse
import com.sep.gothere.data.UserRepository
import com.sep.gothere.util.customCombine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        const val MINIMUM_PASSWORD_LENGTH = 8
    }

    // region Sign Up State
    sealed class SignUpRequestEvent {
        data class SignUpRequestLoading(val fetchLoading: LoginResponse?) : SignUpRequestEvent()

        data class SignUpRequestSuccessful(val response: LoginResponse) : SignUpRequestEvent()

        data class SignUpRequestError(val error: Throwable) : SignUpRequestEvent()
    }

    private val signUpRequestEventChannel = Channel<SignUpRequestEvent>()
    val signUpEvents = signUpRequestEventChannel.receiveAsFlow()

    suspend fun signUpButtonClicked(
        name: String, surname: String, username: String, email: String, password: String
    ) {
        userRegisterVM(
            UserRegisterRequest(
                userName = username,
                name = name,
                surname = surname,
                mail = email,
                password = password
            )
        )
    }

    private suspend fun userRegisterVM(
        userRegisterRequest: UserRegisterRequest
    ) = userRepository.userRegisterRP(userRegisterRequest, onFetchLoading = {
        viewModelScope.launch {
            signUpRequestEventChannel.send(
                SignUpRequestEvent.SignUpRequestLoading(null)
            )
        }
    }, onFetchSuccess = {
        viewModelScope.launch {
            signUpRequestEventChannel.send(
                SignUpRequestEvent.SignUpRequestSuccessful(it)
            )
        }
    }, onFetchFailed = { throwable ->
        viewModelScope.launch {
            signUpRequestEventChannel.send(
                SignUpRequestEvent.SignUpRequestError(throwable)
            )
        }
    })
    // endregion

    // region Name Field State
    enum class NameFieldStatus {
        INITIAL, VALID
    }

    private val nameFieldStateEventChannel = Channel<NameFieldStatus>()
    val nameFieldStateEvents = nameFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, NameFieldStatus.INITIAL)

    suspend fun validateNameField(name: String) {
        if (name.isBlank()) nameFieldStateEventChannel.send(NameFieldStatus.INITIAL)
        else nameFieldStateEventChannel.send(NameFieldStatus.VALID)
    }
    // endregion

    // region Surname Field State
    enum class SurnameFieldState {
        INITIAL, VALID
    }

    private val surnameFieldStateEventChannel = Channel<SurnameFieldState>()
    val surnameFieldStateEvents = surnameFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, SurnameFieldState.INITIAL)

    suspend fun validateSurnameField(surname: String) {
        if (surname.isBlank()) surnameFieldStateEventChannel.send(SurnameFieldState.INITIAL)
        else surnameFieldStateEventChannel.send(SurnameFieldState.VALID)
    }
    // endregion

    // region Username Field State
    enum class UsernameFieldState {
        INITIAL, LOADING_NETWORK_REQUEST, FAILED_NETWORK_REQUEST, INVALID_ALREADY_IN_USE, VALID
    }

    private val usernameFieldStateEventChannel = Channel<UsernameFieldState>()
    val usernameFieldStateEvents = usernameFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, UsernameFieldState.INITIAL)

    suspend fun validateUsernameField(username: String) {
        if (username.isBlank()) usernameFieldStateEventChannel.send(UsernameFieldState.INITIAL)
        else usernameCheckVM(username)
    }

    private suspend fun usernameCheckVM(
        username: String
    ) = userRepository.usernameCheckRP(username, onFetchLoading = {
        viewModelScope.launch {
            usernameFieldStateEventChannel.send(
                UsernameFieldState.LOADING_NETWORK_REQUEST
            )
        }
    }, onFetchSuccess = {
        viewModelScope.launch {
            if (it.success) usernameFieldStateEventChannel.send(UsernameFieldState.VALID)
            else usernameFieldStateEventChannel.send(UsernameFieldState.INVALID_ALREADY_IN_USE)
        }
    }, onFetchFailed = {
        viewModelScope.launch {
            usernameFieldStateEventChannel.send(UsernameFieldState.FAILED_NETWORK_REQUEST)
        }
    })
    // endregion

    // region Email Field State
    enum class EmailFieldState {
        INITIAL, LOADING_NETWORK_REQUEST, FAILED_NETWORK_REQUEST, INVALID_FORMAT, INVALID_ALREADY_IN_USE, VALID
    }

    private val emailFieldStateEventChannel = Channel<EmailFieldState>()
    val emailFieldStateEvents = emailFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, EmailFieldState.INITIAL)

    suspend fun validateEmailField(email: String) {
        if (email.isBlank()) emailFieldStateEventChannel.send(EmailFieldState.INITIAL)
        else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailFieldStateEventChannel.send(EmailFieldState.INVALID_FORMAT)
            } else emailCheckVM(email)
        }
    }

    private suspend fun emailCheckVM(
        email: String
    ) = userRepository.emailCheckRP(email, onFetchLoading = {
        viewModelScope.launch {
            emailFieldStateEventChannel.send(EmailFieldState.LOADING_NETWORK_REQUEST)
        }
    }, onFetchSuccess = {
        viewModelScope.launch {
            if (it.success) emailFieldStateEventChannel.send(EmailFieldState.VALID)
            else emailFieldStateEventChannel.send(EmailFieldState.INVALID_ALREADY_IN_USE)
        }
    }, onFetchFailed = {
        viewModelScope.launch {
            emailFieldStateEventChannel.send(EmailFieldState.FAILED_NETWORK_REQUEST)
        }
    })
    // endregion

    // region Password Field State
    enum class PasswordFieldState {
        INITIAL, INVALID_TOO_SHORT, VALID
    }

    private val passwordFieldStateEventChannel = Channel<PasswordFieldState>()
    val passwordFieldStateEvents = passwordFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, PasswordFieldState.INITIAL)

    suspend fun validatePasswordField(password: String) {
        if (password.isBlank()) passwordFieldStateEventChannel.send(PasswordFieldState.INITIAL)
        else {
            if (password.length < MINIMUM_PASSWORD_LENGTH) {
                passwordFieldStateEventChannel.send(PasswordFieldState.INVALID_TOO_SHORT)
            } else passwordFieldStateEventChannel.send(PasswordFieldState.VALID)
        }
    }
    // endregion

    // region ConfirmPassword Field State
    enum class ConfirmPasswordFieldState {
        INITIAL, INVALID_PASSWORDS_DO_NOT_MATCH, VALID
    }

    private val confirmPasswordFieldStateEventChannel = Channel<ConfirmPasswordFieldState>()
    val confirmPasswordFieldStateEvents = confirmPasswordFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, ConfirmPasswordFieldState.INITIAL)

    suspend fun validateConfirmPasswordField(confirmPassword: String, password: String) {
        if (confirmPassword.isBlank()) confirmPasswordFieldStateEventChannel.send(
            ConfirmPasswordFieldState.INITIAL
        )
        else {
            if (confirmPassword != password) {
                confirmPasswordFieldStateEventChannel.send(ConfirmPasswordFieldState.INVALID_PASSWORDS_DO_NOT_MATCH)
            } else confirmPasswordFieldStateEventChannel.send(ConfirmPasswordFieldState.VALID)
        }
    }
    // endregion

    // region TermsConditions Field State
    enum class TermsConditionsFieldState {
        INITIAL, INVALID, VALID
    }

    private val termsConditionsFieldStateEventChannel = Channel<TermsConditionsFieldState>()
    val termsConditionsFieldStateEvents = termsConditionsFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, TermsConditionsFieldState.INITIAL)

    suspend fun validateTermsConditionsField(termsConditionsState: TermsConditionsFieldState) {
        termsConditionsFieldStateEventChannel.send(termsConditionsState)
    }
    // endregion

    val isSignUpButtonEnabled: Flow<Boolean> = customCombine(
        nameFieldStateEvents,
        surnameFieldStateEvents,
        usernameFieldStateEvents,
        emailFieldStateEvents,
        passwordFieldStateEvents,
        confirmPasswordFieldStateEvents,
        termsConditionsFieldStateEvents
    ) { nameState, surnameState, usernameState, emailState, passwordState, confirmPasswordState, termsConditionsState ->
        val isNameFieldValid = nameState == NameFieldStatus.VALID
        val isSurnameFieldValid = surnameState == SurnameFieldState.VALID
        val isUsernameStateValid = usernameState == UsernameFieldState.VALID
        val isEmailStateValid = emailState == EmailFieldState.VALID
        val isPasswordStateValid = passwordState == PasswordFieldState.VALID
        val isConfirmPasswordStateValid = confirmPasswordState == ConfirmPasswordFieldState.VALID
        val isTermsConditionsStateValid = termsConditionsState == TermsConditionsFieldState.VALID
        return@customCombine (isNameFieldValid and isSurnameFieldValid and isUsernameStateValid and isEmailStateValid and isPasswordStateValid and isConfirmPasswordStateValid and isTermsConditionsStateValid)
    }
}