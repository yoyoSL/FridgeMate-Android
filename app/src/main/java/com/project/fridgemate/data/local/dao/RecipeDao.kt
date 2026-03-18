package com.project.fridgemate.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.fridgemate.data.local.entity.RecipeEntity

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE type = :type ORDER BY cachedAt DESC")
    fun getByType(type: String): LiveData<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Query("DELETE FROM recipes WHERE type = :type")
    suspend fun deleteByType(type: String)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE title = :title AND type = 'RECOMMENDED'")
    suspend fun updateFavoriteByTitle(title: String, isFavorite: Boolean)

    @Query("DELETE FROM recipes WHERE title = :title AND type = :type")
    suspend fun deleteByTitleAndType(title: String, type: String)
}
