package com.project.fridgemate.data.remote.api

import com.project.fridgemate.data.remote.dto.GenerateRecipesRequest
import com.project.fridgemate.data.remote.dto.GenerateRecipesResponse
import com.project.fridgemate.data.remote.dto.PaginatedResponse
import com.project.fridgemate.data.remote.dto.RecipeDto
import com.project.fridgemate.data.remote.dto.SaveRecipeRequest
import com.project.fridgemate.data.remote.dto.SaveRecipeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {

    @POST("ai/recipes/generate")
    suspend fun generateRecipes(@Body request: GenerateRecipesRequest): Response<GenerateRecipesResponse>

    @POST("recipes/save")
    suspend fun saveToFavorites(@Body request: SaveRecipeRequest): Response<SaveRecipeResponse>

    @GET("user/me/recipes")
    suspend fun getUserRecipes(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<RecipeDto>>

    @DELETE("user/me/recipes/{id}")
    suspend fun deleteFromFavorites(@Path("id") id: String): Response<Unit>
}
