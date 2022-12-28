package com.sep.gothere.api.model.image.response

data class ImageResponse(
    val data: String,
    val success: Boolean,
    val message: String?,
    val exceptionType: String?,
    val statusCode: Int?,
    val messages: String?,
    val totalCount: Int?,
    val modelStateErrors: String?
)
