package com.project.fridgemate.ui.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.databinding.ItemRecipeBinding

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onFavoriteClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        with(holder.binding) {
            tvRecipeName.text = recipe.name
            tvRecipeTime.text = "${recipe.timeMinutes} min"
            tvRecipeDifficulty.text = recipe.difficulty
            tvCalories.text = recipe.calories.toString()
            tvFat.text = "${recipe.fat}g"
            tvCarbs.text = "${recipe.carbs}g"
            tvProtein.text = "${recipe.protein}g"

            btnFavorite.setOnClickListener {
                onFavoriteClick(recipe)
            }
        }
    }

    override fun getItemCount() = recipes.size
}