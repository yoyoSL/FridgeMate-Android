package com.project.fridgemate.ui.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.fridgemate.data.local.AppDatabase
import com.project.fridgemate.data.local.entity.RecipeEntity
import com.project.fridgemate.data.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository

    val recommended: LiveData<List<RecipeEntity>>
    val favorites: LiveData<List<RecipeEntity>>

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private var lastIngredients = listOf("chicken", "rice", "tomato", "eggs", "cheese")

    init {
        val dao = AppDatabase.getInstance(application).recipeDao()
        repository = RecipeRepository(dao)
        recommended = repository.getRecommended()
        favorites = repository.getFavorites()

        viewModelScope.launch { repository.fetchFavorites() }
    }

    fun loadRecommended(
        ingredients: List<String> = lastIngredients,
        allergies: List<String>? = null,
        dietPreference: String? = null
    ) {
        lastIngredients = ingredients
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            val result = repository.fetchRecommended(ingredients, allergies, dietPreference)
            _isLoading.value = false
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load recipes"
            }
        }
    }

    fun toggleFavoriteFromRecommended(recipe: RecipeEntity) {
        viewModelScope.launch {
            if (recipe.isFavorite) {
                repository.unfavoriteFromRecommended(recipe)
            } else {
                repository.saveToFavorites(recipe)
            }
        }
    }

    fun removeFromFavorites(recipe: RecipeEntity) {
        viewModelScope.launch {
            repository.removeFromFavorites(recipe)
        }
    }
}
