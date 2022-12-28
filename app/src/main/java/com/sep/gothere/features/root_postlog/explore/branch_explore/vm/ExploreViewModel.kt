package com.sep.gothere.features.root_postlog.explore.branch_explore.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sep.gothere.api.model.venue.get.request.GetVenueRequest
import com.sep.gothere.api.model.venue.get.response.GetVenueResponse
import com.sep.gothere.api.model.venue.get.response.GetVenueResponseData
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.features.root_postlog.explore.branch_explore.adapter.ExploreRecyclerViewAdapter
import com.sep.gothere.features.root_postlog.explore.branch_explore.model.ExploreWidgetBasicVisit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val networkVenueRepository: NetworkVenueRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private var _adapter: ExploreRecyclerViewAdapter? = null
    val adapter get() = _adapter

    private var _heroCardContent: GetVenueResponseData? = null
    val heroCardContent get() = _heroCardContent

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
            _adapter = ExploreRecyclerViewAdapter(
                networkVenueRepository,
                createExploreWidgetBasicVisitList(it.data)
            )
            if (it.data != null) _heroCardContent = it.data[0]
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

    private fun createExploreWidgetBasicVisitList(data: List<GetVenueResponseData>?): List<ExploreWidgetBasicVisit> {
        val exploreWidgetBasicVisitList = ArrayList<ExploreWidgetBasicVisit>()
        if (data == null) return exploreWidgetBasicVisitList
        for (venueResponseData in data) {
            exploreWidgetBasicVisitList.add(
                ExploreWidgetBasicVisit(
                    venueResponseData.id,
                    venueResponseData.name,
                    venueResponseData.shortDescription,
                    venueResponseData.username
                )
            )
        }
        exploreWidgetBasicVisitList.removeAt(0)
        return exploreWidgetBasicVisitList
    }

    sealed class Event {
        data class VenueLoading(val fetchLoading: GetVenueResponse?) : Event()

        data class VenueSuccessful(val response: GetVenueResponse) : Event()

        data class VenueError(val error: Throwable) : Event()
    }
}