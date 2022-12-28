package com.sep.gothere.api.model.venue.put.request

data class PutVenueRequest(
    val id: Long = 0,
    val username: String,
    val name: String,
    val mail: String,
    val phone: String,
    val address: String,
    val shortDescription: String,
    val biography: String,
    val profilImage: String,
    val coverImage: String
)
