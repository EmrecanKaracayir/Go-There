package com.sep.gothere.api.model.login.response

data class LoginResponse(
    val data: LoginResponseData?,
    val success: Boolean,
    val message: String?,
    val exceptionType: String?,
    val statusCode: Int?,
    val messages: String?,
    val totalCount: Int?,
    val modelStateErrors: String?
)
