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
    private val fridgeRepository = FridgeRepository(application.applicationContext)
    private val inventoryRepository = InventoryItemRepository(application.applicationContext)

    val recommended: LiveData<List<RecipeEntity>>
    val favorites: LiveData<List<RecipeEntity>>

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _noFridge = MutableLiveData(false)
    val noFridge: LiveData<Boolean> = _noFridge

    private val _fridgeEmpty = MutableLiveData(false)
    val fridgeEmpty: LiveData<Boolean> = _fridgeEmpty

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
            } else {
                _noFridge.value = false
            }
        }
    }

    fun loadRecommended() {
        _error.value = null
        viewModelScope.launch {
            val ingredients = fetchFridgeIngredients()
            if (ingredients == null) {
                return@launch
            }
            if (ingredients.isEmpty()) {
                _fridgeEmpty.value = true
                return@launch
            }
            _fridgeEmpty.value = false
            _isLoading.value = true
            val result = repository.fetchRecommended(ingredients)
            _isLoading.value = false
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load recipes"
            }
        }
    }

    private suspend fun fetchFridgeIngredients(): List<String>? {
        return when (val fridgeResult = fridgeRepository.getMyFridge()) {
            is FridgeResult.Success -> {
                _noFridge.postValue(false)
                val items = inventoryRepository.getItems(fridgeResult.data.id)
                items.map { it.name }
            }
            is FridgeResult.NoFridge -> {
                _noFridge.postValue(true)
                repository.clearRecommendedCache()
                null
            }
            is FridgeResult.Error -> {
                _error.value = fridgeResult.message
                null
            }
        }
    }

    fun toggleFavorite(recipe: RecipeEntity) {
        val serverId = recipe.serverId ?: return
        val wasFavorite = recipe.isFavorite
        
        viewModelScope.launch {
            val result = if (wasFavorite) {
                repository.unfavoriteRecipe(serverId)
            } else {
                repository.favoriteRecipe(serverId)
            }
            
            if (result.isFailure) {
                _error.postValue("Failed to update favorite: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}
