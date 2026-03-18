package com.project.fridgemate.ui.recipes

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.R
import com.project.fridgemate.data.local.entity.RecipeEntity
import com.project.fridgemate.databinding.ItemRecipeBinding
import com.squareup.picasso.Picasso

class RecipeAdapter(
    private val onFavoriteClick: (RecipeEntity) -> Unit
) : ListAdapter<RecipeEntity, RecipeAdapter.RecipeViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<RecipeEntity>() {
            override fun areItemsTheSame(a: RecipeEntity, b: RecipeEntity) = a.id == b.id
            override fun areContentsTheSame(a: RecipeEntity, b: RecipeEntity) = a == b
        }
    }

    inner class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        with(holder.binding) {
            tvRecipeName.text = recipe.title
            tvRecipeTime.text = recipe.cookingTime
            tvRecipeDifficulty.text = recipe.difficulty
            tvCalories.text = recipe.calories.replace(Regex("[^\\d]"), "").ifEmpty { "-" }
            tvFat.text = recipe.fat.ifEmpty { "-" }
            tvCarbs.text = recipe.carbs.ifEmpty { "-" }
            tvProtein.text = recipe.protein.ifEmpty { "-" }

            if (recipe.imageUrl.isNotBlank()) {
                Picasso.get()
                    .load(recipe.imageUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.light_teal)
                    .error(R.color.light_teal)
                    .into(ivRecipeImage)
            } else {
                ivRecipeImage.setImageResource(R.color.light_teal)
            }

            updateFavoriteIcon(btnFavorite, recipe.isFavorite)
            btnFavorite.setOnClickListener { onFavoriteClick(recipe) }
        }
    }

    private fun updateFavoriteIcon(btn: ImageButton, isFavorite: Boolean) {
        if (isFavorite) {
            btn.setImageResource(R.drawable.ic_star_filled)
            btn.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFD700"))
        } else {
            btn.setImageResource(R.drawable.ic_star_outline)
            btn.imageTintList = ColorStateList.valueOf(Color.parseColor("#2D6A4F"))
        }
    }
}
