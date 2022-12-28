package com.sep.gothere.api.model.me.response

data class MeResponseData(
    val id: Int,
    val userName: String,
    val name: String,
    val surname: String,
    val mail: String,
    val password: String?,
    val il: String?,
    val ilce: String?,
    val isDeleted: Boolean
)
