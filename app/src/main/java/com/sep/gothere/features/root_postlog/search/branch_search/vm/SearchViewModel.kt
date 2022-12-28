package com.sep.gothere.features.root_postlog.search.branch_search.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.api.model.venue.get.response.GetVenueResponse
import com.sep.gothere.api.model.venue.get.response.GetVenueResponseData
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.features.root_postlog.search.branch_search.adapter.SearchRecyclerViewAdapter
import com.sep.gothere.features.root_postlog.search.branch_search.model.SearchWidgetBasicVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val networkVenueRepository: NetworkVenueRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private var _adapter: SearchRecyclerViewAdapter? = null
    val adapter get() = _adapter

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
            _adapter = SearchRecyclerViewAdapter(
                networkVenueRepository,
                createSearchWidgetBasicVisitList(it.data)
            )
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

    private fun createSearchWidgetBasicVisitList(data: List<GetVenueResponseData>?): List<SearchWidgetBasicVisit> {
        val searchWidgetBasicVisitList = ArrayList<SearchWidgetBasicVisit>()
        if (data == null) return searchWidgetBasicVisitList
        for (venueResponseData in data) {
            searchWidgetBasicVisitList.add(
                SearchWidgetBasicVisit(
                    venueResponseData.id,
                    venueResponseData.name,
                    venueResponseData.shortDescription,
                    venueResponseData.username
                )
            )
        }
        return searchWidgetBasicVisitList
    }

    sealed class Event {
        data class VenueLoading(val fetchLoading: GetVenueResponse?) : Event()

        data class VenueSuccessful(val response: GetVenueResponse) : Event()

        data class VenueError(val error: Throwable) : Event()
    }
}