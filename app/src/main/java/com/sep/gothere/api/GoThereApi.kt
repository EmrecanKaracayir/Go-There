package com.sep.gothere.api

import com.sep.gothere.api.model.common.response.ApiResponse
import com.sep.gothere.api.model.image.response.ImageResponse
import com.sep.gothere.api.model.login.request.LoginRequest
import com.sep.gothere.api.model.user.register.request.UserRegisterRequest
import com.sep.gothere.api.model.login.response.LoginResponse
import com.sep.gothere.api.model.me.response.MeResponse
import com.sep.gothere.api.model.user.register.response.UserRegisterResponse
import com.sep.gothere.api.model.venue.get.response.GetVenueResponse
import com.sep.gothere.api.model.venue.put.request.PutVenueRequest
import com.sep.gothere.api.model.venue.put.response.PutVenueResponse
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
    suspend fun userLogin(@Body loginRequest: LoginRequest): LoginResponse

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
        @Body userRegisterRequest: UserRegisterRequest
    ): UserRegisterResponse

    @Headers("accept: application/xml")
    @GET("Auth/me")
    suspend fun me(
        @Header("Authorization") authHeader: String
    ): MeResponse

    @Headers("accept: application/xml")
    @GET("Place")
    suspend fun getVenue(
        @Header("Authorization") authHeader: String,
        @Query("placeId") venueId: Long?,
        @Query("Search") search: String?,
        @Query("Rand") random: Boolean?,
        @Query("Take") take: Int?,
        @Query("Skip") skip: Int?,
        @Query("Order") order: Int?,
    ): GetVenueResponse

    @Headers("accept: application/xml")
    @POST("Place/register")
    suspend fun registerVenue(
        @Header("Authorization") authHeader: String, @Body putVenueRequest: PutVenueRequest
    ): PutVenueResponse

    @Headers("accept: application/xml")
    @PUT("Place")
    suspend fun updateVenue(
        @Header("Authorization") authHeader: String, @Body putVenueRequest: PutVenueRequest
    ): PutVenueResponse

    @Headers("accept: application/xml")
    @GET("Place/UsernameCheck")
    suspend fun checkIfVenueUsernameIsApplicable(
        @Query("username") username: String
    ): ApiResponse

    @Headers("accept: application/xml")
    @GET("Customer/Place")
    suspend fun getUserVenue(
        @Header("Authorization") authHeader: String,
        @Query("placeId") venueId: Long?,
        @Query("Search") search: String?,
        @Query("Rand") random: Boolean?,
        @Query("Take") take: Int?,
        @Query("Skip") skip: Int?,
        @Query("Order") order: Int?,
    ): GetVenueResponse

    @Headers("accept: application/xml")
    @GET("Place/profilImage")
    suspend fun getVenueProfileImage(
        @Header("Authorization") authHeader: String, @Query("id") venueId: Long,
    ): ImageResponse

    @Headers("accept: application/xml")
    @GET("Place/coverImage")
    suspend fun getVenueCoverImage(
        @Header("Authorization") authHeader: String, @Query("id") venueId: Long,
    ): ImageResponse
}