package com.project.fridgemate.data.remote.api

import com.project.fridgemate.data.remote.dto.GenerateRecipesRequest
import com.project.fridgemate.data.remote.dto.GenerateRecipesResponse
import com.project.fridgemate.data.remote.dto.PaginatedResponse
import com.project.fridgemate.data.remote.dto.ServerRecipeDto
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

    @POST("recipes/{id}/favorite")
    suspend fun favoriteRecipe(@Path("id") id: String): Response<Any>

    @DELETE("recipes/{id}/favorite")
    suspend fun unfavoriteRecipe(@Path("id") id: String): Response<Any>

    @GET("user/me/recipes")
    suspend fun getUserFavorites(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<ServerRecipeDto>>

    @GET("recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: String): Response<ServerRecipeDto>
}
