package com.project.fridgemate.data.repository

import com.project.fridgemate.data.remote.ApiClient
import com.project.fridgemate.data.remote.api.InventoryItemApi
import com.project.fridgemate.data.remote.dto.InventoryItemDto

class InventoryItemRepository {

    private val api = ApiClient.createApi(InventoryItemApi::class.java)

    suspend fun getItems(fridgeId: String): List<InventoryItemDto> {
        return try {
            val response = api.getItems(fridgeId)
            if (response.isSuccessful) response.body()?.items ?: emptyList()
            else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
