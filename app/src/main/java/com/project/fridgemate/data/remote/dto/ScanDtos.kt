package com.project.fridgemate.data.remote.dto

data class DetectedItemDto(
    val name: String,
    val quantity: String
)

data class ScanDto(
    val id: String,
    val fridgeId: String,
    val userId: String,
    val status: String,
    val detectedItems: List<DetectedItemDto>,
    val addedItemIds: List<String>,
    val error: String?,
    val createdAt: String,
    val updatedAt: String
)
