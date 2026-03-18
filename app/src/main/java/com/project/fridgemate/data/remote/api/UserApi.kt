package com.project.fridgemate.data.remote.api

import com.project.fridgemate.data.remote.dto.UpdateProfileRequest
import com.project.fridgemate.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @GET("user/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<UserDto>

    @PUT("user/{id}")
    suspend fun updateProfile(
        @Path("id") id: String,
        @Body request: UpdateProfileRequest
    ): Response<UserDto>
}
