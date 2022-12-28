package com.sep.gothere.api.model.user.register.response

import java.util.Date

data class UserRegisterResponseData(
    val accessToken: String,
    val accessTokenExpirationTime: Date,
    val refreshToken: String
)
