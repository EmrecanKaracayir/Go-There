package com.sep.gothere.api

import com.sep.gothere.api.model.request.LoginRequest
import com.sep.gothere.api.model.request.SignUpRequest
import com.sep.gothere.api.model.response.ApiResponse
import retrofit2.http.*

interface GoThereApi {

    companion object {
        const val BASE_URL_API = "http://10.0.2.2:5213/api/"
    }

    @Headers(
        "accept: */*",
        "Content-type: application/json",
    )
    @POST("Auth/Login/")
    suspend fun userLogin(@Body loginRequest: LoginRequest): ApiResponse

    @Headers("accept: application/xml")
    @GET("Auth/UsernameControl")
    suspend fun checkIfUsernameIsApplicable(
        @Query("username") username: String
    ): ApiResponse

    @Headers("accept: application/xml")
    @GET("Auth/EmailControl")
    suspend fun checkIfEmailIsApplicable(
        @Query("mail") email: String
    ): ApiResponse

    @Headers("accept: application/xml")
    @POST("Auth/Register/")
    suspend fun userRegister(
        @Body signUpRequest: SignUpRequest
    ): ApiResponse
}