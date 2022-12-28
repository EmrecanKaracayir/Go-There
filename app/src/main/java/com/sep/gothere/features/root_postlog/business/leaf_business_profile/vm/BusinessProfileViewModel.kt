package com.sep.gothere.features.root_postlog.business.leaf_business_profile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.api.model.venue.get.response.GetVenueResponse
import com.sep.gothere.data.NetworkVenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessProfileViewModel @Inject constructor(
    private val networkVenueRepository: NetworkVenueRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    suspend fun getVenueVM(
        getVenueRequest: GetVenueRequest
    ) = networkVenueRepository.getVenueRP(
        getVenueRequest,
        onFetchLoading = {
            viewModelScope.launch {
                eventChannel.send(
                    Event.VenueLoading(null)
                )
            }
        },
        onFetchSuccess = {
            viewModelScope.launch {
                eventChannel.send(
                    Event.VenueSuccessful(it)
                )
            }
        },
        onFetchFailed = { throwable ->
            viewModelScope.launch {
                eventChannel.send(
                    Event.VenueError(throwable)
                )
            }
        }
    )

    sealed class Event {
        data class VenueLoading(val fetchLoading: GetVenueResponse?) : Event()

        data class VenueSuccessful(val response: GetVenueResponse) : Event()

        data class VenueError(val error: Throwable) : Event()
    }
}