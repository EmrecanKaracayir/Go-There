package com.sep.gothere.api.model.response

data class ApiResponse(
    val data: LoginResponseData?,
    val success: Boolean,
    val message: String?,
    val exceptionType: String?,
    val statusCode: Int?,
    val messages: String?,
    val totalCount: Int?,
    val modelStateErrors: String?
)
