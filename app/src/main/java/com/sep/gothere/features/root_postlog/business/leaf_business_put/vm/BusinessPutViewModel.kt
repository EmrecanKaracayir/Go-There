package com.sep.gothere.features.root_postlog.business.leaf_business_put.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.api.model.venue.get.response.GetVenueResponse
import com.sep.gothere.api.model.venue.put.request.PutVenueRequest
import com.sep.gothere.api.model.venue.put.response.PutVenueResponse
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.util.customCombine10
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessPutViewModel @Inject constructor(
    private val networkVenueRepository: NetworkVenueRepository
) : ViewModel() {

    sealed class VenueGetRequestEvent {
        data class VenueGetRequestLoading(val fetchLoading: GetVenueResponse?) : VenueGetRequestEvent()

        data class VenueGetRequestSuccessful(val response: GetVenueResponse) : VenueGetRequestEvent()

        data class VenueGetRequestError(val error: Throwable) : VenueGetRequestEvent()
    }

    var isVenueFetched = false

    private val venueGetRequestEventChannel = Channel<VenueGetRequestEvent>()
    val venueGetEvents = venueGetRequestEventChannel.receiveAsFlow()

    suspend fun getVenueVM(
        getVenueRequest: GetVenueRequest
    ) = networkVenueRepository.getVenueRP(getVenueRequest, onFetchLoading = {
        viewModelScope.launch {
            venueGetRequestEventChannel.send(
                VenueGetRequestEvent.VenueGetRequestLoading(null)
            )
        }
    }, onFetchSuccess = {
        isVenueFetched = true
        viewModelScope.launch {
            venueGetRequestEventChannel.send(
                VenueGetRequestEvent.VenueGetRequestSuccessful(it)
            )
        }
    }, onFetchFailed = { throwable ->
        viewModelScope.launch {
            venueGetRequestEventChannel.send(
                VenueGetRequestEvent.VenueGetRequestError(throwable)
            )
        }
    })

    sealed class VenuePutRequestEvent {
        data class VenuePutRequestLoading(val fetchLoading: PutVenueResponse?) : VenuePutRequestEvent()

        data class VenuePutRequestSuccessful(val response: PutVenueResponse) : VenuePutRequestEvent()

        data class VenuePutRequestError(val error: Throwable) : VenuePutRequestEvent()
    }

    private val venuePutRequestEventChannel = Channel<VenuePutRequestEvent>()
    val venuePutEvents = venuePutRequestEventChannel.receiveAsFlow()

    suspend fun bPutButtonClickedRegister(
        venueID: Long, name: String, venueUsername: String, desc: String, bio: String, email: String, phone: String, address: String, profileImage: String, coverImage: String
    ) {
        venueRegisterVM(
            PutVenueRequest(
                id = venueID,
                username = venueUsername,
                name = name,
                mail = email,
                phone = phone,
                address = address,
                shortDescription = desc,
                biography = bio,
                profilImage = profileImage,
                coverImage = coverImage
            )
        )
    }

    private suspend fun venueRegisterVM(
        putVenueRequest: PutVenueRequest
    ) = networkVenueRepository.registerVenueRP(putVenueRequest, onFetchLoading = {
        viewModelScope.launch {
            venuePutRequestEventChannel.send(
                VenuePutRequestEvent.VenuePutRequestLoading(null)
            )
        }
    }, onFetchSuccess = {
        viewModelScope.launch {
            venuePutRequestEventChannel.send(
                VenuePutRequestEvent.VenuePutRequestSuccessful(it)
            )
        }
    }, onFetchFailed = { throwable ->
        viewModelScope.launch {
            venuePutRequestEventChannel.send(
                VenuePutRequestEvent.VenuePutRequestError(throwable)
            )
        }
    })

    suspend fun bPutButtonClickedUpdate(
        venueID: Long, name: String, venueUsername: String, desc: String, bio: String, email: String, phone: String, address: String, profileImage: String, coverImage: String
    ) {
        venueUpdateVM(
            PutVenueRequest(
                id = venueID,
                username = venueUsername,
                name = name,
                mail = email,
                phone = phone,
                address = address,
                shortDescription = desc,
                biography = bio,
                profilImage = profileImage,
                coverImage = coverImage
            )
        )
    }

    private suspend fun venueUpdateVM(
        putVenueRequest: PutVenueRequest
    ) = networkVenueRepository.updateVenueRP(putVenueRequest, onFetchLoading = {
        viewModelScope.launch {
            venuePutRequestEventChannel.send(
                VenuePutRequestEvent.VenuePutRequestLoading(null)
            )
        }
    }, onFetchSuccess = {
        viewModelScope.launch {
            venuePutRequestEventChannel.send(
                VenuePutRequestEvent.VenuePutRequestSuccessful(it)
            )
        }
    }, onFetchFailed = { throwable ->
        viewModelScope.launch {
            venuePutRequestEventChannel.send(
                VenuePutRequestEvent.VenuePutRequestError(throwable)
            )
        }
    })

    enum class NameFieldState {
        INITIAL, VALID
    }

    private val nameFieldStateEventChannel = Channel<NameFieldState>()
    val nameFieldStateEvents = nameFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, NameFieldState.INITIAL)

    suspend fun validateNameField(name: String) {
        if (name.isBlank()) nameFieldStateEventChannel.send(NameFieldState.INITIAL)
        else nameFieldStateEventChannel.send(NameFieldState.VALID)
    }

    enum class VenueUsernameFieldState {
        INITIAL, LOADING_NETWORK_REQUEST, FAILED_NETWORK_REQUEST, INVALID_ALREADY_IN_USE, VALID
    }

    private val venueUsernameFieldStateEventChannel = Channel<VenueUsernameFieldState>()
    val venueUsernameFieldStateEvents = venueUsernameFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, VenueUsernameFieldState.INITIAL)

    suspend fun validateVenueUsernameField(username: String) {
        if (username.isBlank()) venueUsernameFieldStateEventChannel.send(VenueUsernameFieldState.INITIAL)
        else if (!isVenueFetched) venueUsernameCheckVM(username)
    }

    private suspend fun venueUsernameCheckVM(
        username: String
    ) = networkVenueRepository.venueUsernameCheckRP(username, onFetchLoading = {
        viewModelScope.launch {
            venueUsernameFieldStateEventChannel.send(
                VenueUsernameFieldState.LOADING_NETWORK_REQUEST
            )
        }
    }, onFetchSuccess = {
        viewModelScope.launch {
            if (it.success) venueUsernameFieldStateEventChannel.send(VenueUsernameFieldState.VALID)
            else venueUsernameFieldStateEventChannel.send(VenueUsernameFieldState.INVALID_ALREADY_IN_USE)
        }
    }, onFetchFailed = {
        viewModelScope.launch {
            venueUsernameFieldStateEventChannel.send(VenueUsernameFieldState.FAILED_NETWORK_REQUEST)
        }
    })

    enum class DescFieldState {
        INITIAL, VALID
    }

    private val descFieldStateEventChannel = Channel<DescFieldState>()
    val descFieldStateEvents = descFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, DescFieldState.INITIAL)

    suspend fun validateDescField(desc: String) {
        if (desc.isBlank()) descFieldStateEventChannel.send(DescFieldState.INITIAL)
        else descFieldStateEventChannel.send(DescFieldState.VALID)
    }

    enum class BioFieldState {
        INITIAL, VALID
    }

    private val bioFieldStateEventChannel = Channel<BioFieldState>()
    val bioFieldStateEvents = bioFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, BioFieldState.INITIAL)

    suspend fun validateBioField(bio: String) {
        if (bio.isBlank()) bioFieldStateEventChannel.send(BioFieldState.INITIAL)
        else bioFieldStateEventChannel.send(BioFieldState.VALID)
    }

    enum class EmailFieldState {
        INITIAL, INVALID_FORMAT, VALID
    }

    private val emailFieldStateEventChannel = Channel<EmailFieldState>()
    val emailFieldStateEvents = emailFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, EmailFieldState.INITIAL)

    suspend fun validateEmailField(email: String) {
        if (email.isBlank()) emailFieldStateEventChannel.send(EmailFieldState.INITIAL)
        else {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailFieldStateEventChannel.send(EmailFieldState.INVALID_FORMAT)
            } else emailFieldStateEventChannel.send(EmailFieldState.VALID)
        }
    }

    enum class PhoneFieldState {
        INITIAL, VALID
    }

    private val phoneFieldStateEventChannel = Channel<PhoneFieldState>()
    val phoneFieldStateEvents = phoneFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, PhoneFieldState.INITIAL)

    suspend fun validatePhoneField(phone: String) {
        if (phone.isBlank()) phoneFieldStateEventChannel.send(PhoneFieldState.INITIAL)
        else phoneFieldStateEventChannel.send(PhoneFieldState.VALID)
    }

    enum class AddressFieldState {
        INITIAL, VALID
    }

    private val addressFieldStateEventChannel = Channel<AddressFieldState>()
    val addressFieldStateEvents = addressFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, AddressFieldState.INITIAL)

    suspend fun validateAddressField(address: String) {
        if (address.isBlank()) addressFieldStateEventChannel.send(AddressFieldState.INITIAL)
        else addressFieldStateEventChannel.send(AddressFieldState.VALID)
    }

    var profileImageBase64: String? = null

    enum class ProfileImageFieldState {
        INITIAL, INVALID, VALID
    }

    private val profileImageFieldStateEventChannel = Channel<ProfileImageFieldState>()
    val profileImageFieldStateEvents = profileImageFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, ProfileImageFieldState.INITIAL)

    suspend fun validateProfileImageField(profileImageFieldState: ProfileImageFieldState) {
        profileImageFieldStateEventChannel.send(profileImageFieldState)
    }

    var coverImageBase64: String? = null

    enum class CoverImageFieldState {
        INITIAL, INVALID, VALID
    }

    private val coverImageFieldStateEventChannel = Channel<CoverImageFieldState>()
    val coverImageFieldStateEvents = coverImageFieldStateEventChannel.receiveAsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, CoverImageFieldState.INITIAL)

    suspend fun validateCoverImageField(coverImageFieldState: CoverImageFieldState) {
        coverImageFieldStateEventChannel.send(coverImageFieldState)
    }

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

    val isCompleteButtonEnabled: Flow<Boolean> = customCombine10(
        nameFieldStateEvents,
        venueUsernameFieldStateEvents,
        descFieldStateEvents,
        bioFieldStateEvents,
        emailFieldStateEvents,
        phoneFieldStateEvents,
        addressFieldStateEvents,
        profileImageFieldStateEvents,
        coverImageFieldStateEvents,
        termsConditionsFieldStateEvents
    ) { nameState, venueUsernameState, descState, bioState, emailState, phoneState, addressState, profileImageState, coverImageState, termsState ->
        val isNameFieldValid = nameState == NameFieldState.VALID
        val isVenueUsernameValid = venueUsernameState == VenueUsernameFieldState.VALID || isVenueFetched
        val isDescStateValid = descState == DescFieldState.VALID
        val isBioStateValid = bioState == BioFieldState.VALID
        val isEmailStateValid = emailState == EmailFieldState.VALID
        val isPhoneStateValid = phoneState == PhoneFieldState.VALID
        val isAddressStateValid = addressState == AddressFieldState.VALID
        val isProfileImageStateValid = profileImageState == ProfileImageFieldState.VALID
        val isCoverImageStateValid = coverImageState == CoverImageFieldState.VALID
        val isTermStateValid = termsState == TermsConditionsFieldState.VALID
        return@customCombine10 (isNameFieldValid and isVenueUsernameValid and isDescStateValid and isBioStateValid and isEmailStateValid and isPhoneStateValid and isAddressStateValid and isProfileImageStateValid and isCoverImageStateValid and isTermStateValid)
    }
}