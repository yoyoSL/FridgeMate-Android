package com.project.fridgemate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fridge")
data class FridgeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val inviteCode: String,
    val membersJson: String = "[]",
    val cachedAt: Long = System.currentTimeMillis()
)
