package com.sep.gothere.api.model.venue.put.response

data class PutVenueResponse(
    val data: PutVenueResponseData?,
    val success: Boolean,
    val message: String?,
    val exceptionType: String?,
    val statusCode: Int?,
    val messages: String?,
    val totalCount: Int?,
    val modelStateErrors: String?
)
