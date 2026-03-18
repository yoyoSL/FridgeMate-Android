package com.project.fridgemate.data.repository

import com.project.fridgemate.data.remote.ApiClient
import com.project.fridgemate.data.remote.api.UserApi
import com.project.fridgemate.data.remote.dto.UpdateProfileRequest
import com.project.fridgemate.data.remote.dto.UserDto

class UserRepository {

    private val api = ApiClient.createApi(UserApi::class.java)

    suspend fun getUserById(id: String): UserDto? {
        val response = api.getUserById(id)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun updateProfile(id: String, request: UpdateProfileRequest): UserDto? {
        val response = api.updateProfile(id, request)
        return if (response.isSuccessful) response.body() else null
    }
}
