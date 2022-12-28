package com.sep.gothere.api.model.venue.get.response

data class GetVenueResponse(
    val data: List<GetVenueResponseData>?,
    val success: Boolean,
    val message: String?,
    val exceptionType: String?,
    val statusCode: Int?,
    val messages: String?,
    val totalCount: Int?,
    val modelStateErrors: String?
)
