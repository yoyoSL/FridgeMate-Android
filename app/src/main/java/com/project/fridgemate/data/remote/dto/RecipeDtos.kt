package com.project.fridgemate.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Requests ────────────────────────────────────────────────────────────────

data class GenerateRecipesRequest(
    val ingredients: List<String>,
    val allergies: List<String>? = null,
    val dietPreference: String? = null,
    val count: Int = 3
)

data class SaveRecipeRequest(
    val title: String,
    val description: String? = null,
    val cookingTime: String? = null,
    val difficulty: String? = null,
    val ingredients: List<RecipeIngredientDto>? = null,
    val steps: List<String>? = null,
    val nutrition: RecipeNutritionDto? = null,
    val imageUrl: String? = null
)

// ── Responses ───────────────────────────────────────────────────────────────

data class GenerateRecipesResponse(
    val message: String,
    val recipes: List<RecipeDto>,
    val count: Int
)

data class SaveRecipeResponse(
    val message: String,
    val recipe: SavedRecipeDto
)

data class SavedRecipeDto(
    @SerializedName("_id") val id: String,
    val title: String,
    val imageUrl: String?
)

data class RecipeDto(
    val title: String,
    val description: String?,
    val cookingTime: String?,
    val difficulty: String?,
    val ingredients: List<RecipeIngredientDto>?,
    val steps: List<String>?,
    val nutrition: RecipeNutritionDto?,
    val imageUrl: String?
)

data class RecipeIngredientDto(
    val name: String,
    val amount: String
)

data class RecipeNutritionDto(
    val calories: String?,
    val protein: String?,
    val carbs: String?,
    val fat: String?
)
