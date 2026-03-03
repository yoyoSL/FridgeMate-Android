package com.project.fridgemate.ui.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Recipe(
    val id: Int,
    val name: String,
    val timeMinutes: Int,
    val difficulty: String,
    val calories: Int,
    val fat: Int,
    val carbs: Int,
    val protein: Int,
    val imageUrl: String = "",
    val isFavorite: Boolean = false
)

class RecipesViewModel : ViewModel() {

    // TODO list of recommended recipes, need to connect to database
    private val _recommended = MutableLiveData<List<Recipe>>(
        listOf(
            Recipe(1, "Chicken & Spinach Omelette", 15, "Easy", 320, 18, 8, 32),
            Recipe(2, "Tomato Pasta Primavera", 25, "Medium", 410, 12, 58, 14),
            Recipe(3, "Avocado Toast", 10, "Easy", 280, 15, 30, 8)
        )
    )
    val recommended: LiveData<List<Recipe>> = _recommended

    //TODO list of favorite recipes, need to connect to database
    private val _favorites = MutableLiveData<List<Recipe>>(emptyList())
    val favorites: LiveData<List<Recipe>> = _favorites

    fun toggleFavorite(recipe: Recipe) {
        val current = _favorites.value?.toMutableList() ?: mutableListOf()
        if (current.contains(recipe)) {
            current.remove(recipe)
        } else {
            current.add(recipe)
        }
        _favorites.value = current
    }
}