package com.sep.gothere.api.model.user.register.response

data class UserRegisterResponse(
    val data: UserRegisterResponseData?,
    val success: Boolean,
    val message: String?,
    val exceptionType: String?,
    val statusCode: Int?,
    val messages: String?,
    val totalCount: Int?,
    val modelStateErrors: String?
)
