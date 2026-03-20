package com.project.fridgemate.data.repository

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.project.fridgemate.data.local.dao.RecipeDao
import com.project.fridgemate.data.local.entity.RecipeEntity
import com.project.fridgemate.data.remote.ApiClient
import com.project.fridgemate.data.remote.api.RecipeApi
import com.project.fridgemate.data.remote.dto.GenerateRecipesRequest
import com.project.fridgemate.data.remote.dto.RecipeDto
import com.project.fridgemate.data.remote.dto.RecipeIngredientDto
import com.project.fridgemate.data.remote.dto.RecipeNutritionDto
import com.project.fridgemate.data.remote.dto.SaveRecipeRequest

class RecipeRepository(private val recipeDao: RecipeDao) {

    private val recipeApi: RecipeApi = ApiClient.createApi(RecipeApi::class.java)
    private val gson = Gson()

    companion object {
        private const val CACHE_TTL_MS = 30 * 60 * 1000L // 30 minutes
    }

    fun getRecommended(): LiveData<List<RecipeEntity>> {
        return recipeDao.getByType(RecipeEntity.TYPE_RECOMMENDED)
    }

    suspend fun clearRecommendedCache() {
        recipeDao.deleteByType(RecipeEntity.TYPE_RECOMMENDED)
    }

    suspend fun isCacheExpired(): Boolean {
        val lastCache = recipeDao.getLatestCacheTime(RecipeEntity.TYPE_RECOMMENDED) ?: return true
        return System.currentTimeMillis() - lastCache > CACHE_TTL_MS
    }

    fun getFavorites(): LiveData<List<RecipeEntity>> {
        return recipeDao.getByType(RecipeEntity.TYPE_FAVORITE)
    }

    suspend fun fetchRecommended(
        ingredients: List<String>,
        allergies: List<String>? = null,
        dietPreference: String? = null,
        count: Int = 3
    ): Result<Unit> {
        return try {
            val response = recipeApi.generateRecipes(
                GenerateRecipesRequest(ingredients, allergies, dietPreference, count)
            )
            if (response.isSuccessful) {
                val recipes = response.body()?.recipes ?: emptyList()
                val entities = recipes.map { it.toEntity(RecipeEntity.TYPE_RECOMMENDED) }
                recipeDao.deleteByType(RecipeEntity.TYPE_RECOMMENDED)
                recipeDao.insertAll(entities)
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string() ?: "Failed to generate recipes"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchFavorites(): Result<Unit> {
        return try {
            val response = recipeApi.getUserRecipes()
            if (response.isSuccessful) {
                val recipes = response.body()?.items ?: emptyList()
                val entities = recipes.map { it.toEntity(RecipeEntity.TYPE_FAVORITE).copy(isFavorite = true) }
                recipeDao.deleteByType(RecipeEntity.TYPE_FAVORITE)
                recipeDao.insertAll(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to load favorites"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveToFavorites(entity: RecipeEntity): Result<Unit> {
        return try {
            val ingredients = runCatching {
                gson.fromJson(entity.ingredientsJson, Array<RecipeIngredientDto>::class.java).toList()
            }.getOrDefault(emptyList())

            val steps = runCatching {
                gson.fromJson(entity.stepsJson, Array<String>::class.java).toList()
            }.getOrDefault(emptyList())

            val response = recipeApi.saveToFavorites(
                SaveRecipeRequest(
                    title = entity.title,
                    description = entity.description,
                    cookingTime = entity.cookingTime,
                    difficulty = entity.difficulty,
                    ingredients = ingredients,
                    steps = steps,
                    nutrition = RecipeNutritionDto(entity.calories, entity.protein, entity.carbs, entity.fat),
                    imageUrl = entity.imageUrl
                )
            )
            if (response.isSuccessful) {
                val serverId = response.body()?.recipe?.id
                val favoriteEntity = entity.copy(
                    id = 0,
                    serverId = serverId,
                    type = RecipeEntity.TYPE_FAVORITE,
                    isFavorite = true,
                    cachedAt = System.currentTimeMillis()
                )
                recipeDao.insertAll(listOf(favoriteEntity))
                recipeDao.updateFavorite(entity.id, true)
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string() ?: "Failed to save recipe"
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unfavoriteFromRecommended(entity: RecipeEntity): Result<Unit> {
        return try {
            if (entity.serverId != null) {
                recipeApi.deleteFromFavorites(entity.serverId)
            }
            recipeDao.updateFavorite(entity.id, false)
            recipeDao.deleteByTitleAndType(entity.title, RecipeEntity.TYPE_FAVORITE)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromFavorites(entity: RecipeEntity): Result<Unit> {
        return try {
            if (entity.serverId != null) {
                recipeApi.deleteFromFavorites(entity.serverId)
            }
            recipeDao.deleteById(entity.id)
            recipeDao.updateFavoriteByTitle(entity.title, false)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun RecipeDto.toEntity(type: String): RecipeEntity {
        return RecipeEntity(
            title = title,
            description = description ?: "",
            cookingTime = cookingTime ?: "",
            difficulty = difficulty ?: "Medium",
            ingredientsJson = gson.toJson(ingredients ?: emptyList<RecipeIngredientDto>()),
            stepsJson = gson.toJson(steps ?: emptyList<String>()),
            calories = nutrition?.calories ?: "",
            protein = nutrition?.protein ?: "",
            carbs = nutrition?.carbs ?: "",
            fat = nutrition?.fat ?: "",
            imageUrl = imageUrl ?: "",
            type = type
        )
    }
}
