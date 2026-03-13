package com.project.fridgemate.data.repository

import com.project.fridgemate.data.remote.ApiClient
import com.project.fridgemate.data.remote.api.FridgeApi
import com.project.fridgemate.data.remote.dto.CreateFridgeRequest
import com.project.fridgemate.data.remote.dto.FridgeDto
import com.project.fridgemate.data.remote.dto.FridgeMemberDetailDto
import com.project.fridgemate.data.remote.dto.JoinFridgeRequest

sealed class FridgeResult<out T> {
    data class Success<T>(val data: T) : FridgeResult<T>()
    data class Error(val message: String) : FridgeResult<Nothing>()
    data object NoFridge : FridgeResult<Nothing>()
}

class FridgeRepository {

    private val fridgeApi: FridgeApi = ApiClient.createApi(FridgeApi::class.java)

    suspend fun getMyFridge(): FridgeResult<FridgeDto> {
        return try {
            val response = fridgeApi.getMyFridge()
            if (response.isSuccessful) {
                FridgeResult.Success(response.body()!!.data)
            } else if (response.code() == 404) {
                FridgeResult.NoFridge
            } else {
                FridgeResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            FridgeResult.Error(networkErrorMessage(e))
        }
    }

    suspend fun getMembers(): FridgeResult<List<FridgeMemberDetailDto>> {
        return try {
            val response = fridgeApi.getMyFridgeMembers()
            if (response.isSuccessful) {
                FridgeResult.Success(response.body()!!.items)
            } else {
                FridgeResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            FridgeResult.Error(networkErrorMessage(e))
        }
    }

    suspend fun createFridge(name: String): FridgeResult<Unit> {
        return try {
            val response = fridgeApi.createFridge(CreateFridgeRequest(name.trim()))
            if (response.isSuccessful) {
                FridgeResult.Success(Unit)
            } else {
                FridgeResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            FridgeResult.Error(networkErrorMessage(e))
        }
    }

    suspend fun joinFridge(inviteCode: String): FridgeResult<Unit> {
        return try {
            val response = fridgeApi.joinFridge(JoinFridgeRequest(inviteCode.trim().uppercase()))
            if (response.isSuccessful) {
                FridgeResult.Success(Unit)
            } else {
                FridgeResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            FridgeResult.Error(networkErrorMessage(e))
        }
    }

    suspend fun leaveFridge(): FridgeResult<Unit> {
        return try {
            val response = fridgeApi.leaveFridge()
            if (response.isSuccessful) {
                FridgeResult.Success(Unit)
            } else {
                FridgeResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            FridgeResult.Error(networkErrorMessage(e))
        }
    }

    private fun parseError(errorBody: String?): String {
        return errorBody?.takeIf { it.isNotBlank() } ?: "Something went wrong. Please try again."
    }

    private fun networkErrorMessage(e: Exception): String {
        return if (e is java.net.ConnectException || e is java.net.UnknownHostException) {
            "Unable to connect to server. Please check your connection."
        } else {
            e.localizedMessage ?: "An unexpected error occurred."
        }
    }
}
