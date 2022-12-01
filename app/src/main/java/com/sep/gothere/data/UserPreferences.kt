package com.sep.gothere.data

data class UserPreferences(
    val cachedUser: CachedUser?
)

data class CachedUser(
    val cachedUser_id: String,
    val cachedUser_image: String?,
    val cachedUser_name: String,
    val cachedUser_email: String,
    val cachedUser_token: String
)
