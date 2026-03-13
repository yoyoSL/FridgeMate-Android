package com.project.fridgemate.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ────────────────────────────────────────────────────────────────

data class CreateFridgeRequest(val name: String)

data class JoinFridgeRequest(val inviteCode: String)

// ── Responses ───────────────────────────────────────────────────────────────

data class ApiOkResponse<T>(val ok: Boolean, val data: T)

data class CreateFridgeData(val fridgeId: String, val inviteCode: String)

data class JoinFridgeData(val fridgeId: String)

data class FridgeDto(
    @SerializedName("_id") val id: String,
    val name: String,
    val inviteCode: String,
    val members: List<FridgeMemberDto>
)

data class FridgeMemberDto(
    val userId: String,
    val joinedAt: String?
)

data class FridgeMemberDetailDto(
    val userId: String,
    val displayName: String,
    val profileImage: String?
)

data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val limit: Int
)
