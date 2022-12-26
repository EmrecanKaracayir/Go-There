package com.sep.gothere.api.model.user_register.request

data class UserRegisterRequest(
    val id: Int = 0,
    val userName: String,
    val name: String,
    val surname: String,
    val mail: String,
    val password: String,
    val isDeleted: Boolean = false
)
