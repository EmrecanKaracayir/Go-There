package com.sep.gothere.api.model.response

import java.util.Date

data class LoginResponseData(
    val accessToken: String,
    val accessTokenExpirationTime: Date,
    val refreshToken: String
)
