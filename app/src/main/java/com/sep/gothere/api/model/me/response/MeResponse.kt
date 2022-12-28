package com.sep.gothere.api.model.me.response

data class MeResponse(
    val data: MeResponseData?,
    val success: Boolean,
    val message: String?,
    val exceptionType: String?,
    val statusCode: Int?,
    val messages: String?,
    val totalCount: Int?,
    val modelStateErrors: String?
)
