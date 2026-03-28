package com.project.fridgemate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.fridgemate.data.local.entity.FridgeEntity

@Dao
interface FridgeDao {

    @Query("SELECT * FROM fridge LIMIT 1")
    suspend fun get(): FridgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fridge: FridgeEntity)

    @Query("DELETE FROM fridge")
    suspend fun clear()
}
