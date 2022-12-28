package com.sep.gothere.api.model.venue.get.request

data class GetVenueRequest(
    val venueId: Long? = null,
    val search: String? = null,
    val random: Boolean? = false,
    val take: Int? = null,
    val skip: Int? = null,
    val order: Int? = null,
)