package com.sep.gothere.api.model.venue.get.response

data class GetVenueResponseData(
    val id: Long,
    val username: String,
    val name: String,
    val mail: String,
    val phone: String,
    val address: String,
    val favCount: Long,
    val customerId: Long,
    val shortDescription: String,
    val biography: String
)
