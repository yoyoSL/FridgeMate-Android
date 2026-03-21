package com.project.fridgemate.ui.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.fridgemate.data.local.AppDatabase
import com.project.fridgemate.data.local.entity.RecipeEntity
import com.project.fridgemate.data.repository.FridgeRepository
import com.project.fridgemate.data.repository.FridgeResult
import com.project.fridgemate.data.repository.InventoryItemRepository
import com.project.fridgemate.data.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    private val fridgeRepository = FridgeRepository()
    private val inventoryRepository = InventoryItemRepository()

    val recommended: LiveData<List<RecipeEntity>>
    val favorites: LiveData<List<RecipeEntity>>

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        val dao = AppDatabase.getInstance(application).recipeDao()
        repository = RecipeRepository(dao)
        recommended = repository.getRecommended()
        favorites = repository.getFavorites()

        viewModelScope.launch { repository.fetchFavorites() }
    }

    fun loadRecommendedIfNeeded() {
        viewModelScope.launch {
            if (repository.isCacheExpired()) {
                loadRecommended()
            }
        }
    }

    fun loadRecommended(
        allergies: List<String>? = null,
        dietPreference: String? = null
    ) {
        _error.value = null
        viewModelScope.launch {
            val ingredients = fetchFridgeIngredients()
            if (ingredients == null) {
                return@launch
            }
            if (ingredients.isEmpty()) {
                _error.value = "Your fridge is empty. Add items to generate recipes."
                return@launch
            }
            _isLoading.value = true
            val result = repository.fetchRecommended(ingredients, allergies, dietPreference)
            _isLoading.value = false
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load recipes"
            }
        }
    }

    private suspend fun fetchFridgeIngredients(): List<String>? {
        return when (val fridgeResult = fridgeRepository.getMyFridge()) {
            is FridgeResult.Success -> {
                val items = inventoryRepository.getItems(fridgeResult.data.id)
                items.map { it.name }
            }
            is FridgeResult.NoFridge -> {
                _error.value = "No active fridge. Create or join a fridge first."
                null
            }
            is FridgeResult.Error -> {
                _error.value = fridgeResult.message
                null
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
