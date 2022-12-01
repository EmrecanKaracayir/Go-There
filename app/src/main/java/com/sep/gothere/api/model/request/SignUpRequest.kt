package com.sep.gothere.api.model.request

data class SignUpRequest(
    val id: Int = 0,
    val userName: String,
    val name: String,
    val surname: String,
    val mail: String,
    val password: String,
    val isDeleted: Boolean = false
)
