package com.project.fridgemate.data.repository

import com.project.fridgemate.data.remote.ApiClient
import com.project.fridgemate.data.remote.api.UserApi
import com.project.fridgemate.data.remote.dto.UpdateProfileRequest
import com.project.fridgemate.data.remote.dto.UserDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    suspend fun uploadProfileImage(imageBytes: ByteArray, mimeType: String): String? {
        val extension = when (mimeType) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
        val requestBody = imageBytes.toRequestBody(mimeType.toMediaType())
        val part = MultipartBody.Part.createFormData("image", "profile.$extension", requestBody)
        val response = api.uploadProfileImage(part)
        return if (response.isSuccessful) response.body()?.data?.imageUrl else null
    }
}
