package com.project.fridgemate.ui.recipes

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.fridgemate.BuildConfig
import com.project.fridgemate.R
import com.project.fridgemate.data.local.AppDatabase
import com.project.fridgemate.data.local.entity.RecipeEntity
import com.project.fridgemate.data.remote.dto.RecipeIngredientDto
import com.project.fridgemate.databinding.FragmentRecipeDetailBinding
import com.squareup.picasso.Picasso

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private val viewModel: RecipesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val recipeId = arguments?.getLong("recipeId") ?: return

        val dao = AppDatabase.getInstance(requireContext()).recipeDao()
        dao.getById(recipeId).observe(viewLifecycleOwner) { recipe ->
            if (recipe == null) return@observe

            binding.tvTitle.text = recipe.title
            binding.tvDescription.text = recipe.description.ifBlank { "A delicious recipe just for you." }
            binding.chipTime.text = recipe.cookingTime.ifBlank { "—" }
            binding.chipDifficulty.text = recipe.difficulty

            binding.tvCalories.text = recipe.calories.ifBlank { "—" }
            binding.tvProtein.text = recipe.protein.ifBlank { "—" }
            binding.tvCarbs.text = recipe.carbs.ifBlank { "—" }
            binding.tvFat.text = recipe.fat.ifBlank { "—" }

            if (recipe.imageUrl.isNotBlank()) {
                val fullUrl = if (recipe.imageUrl.startsWith("/")) {
                    BuildConfig.BASE_URL.trimEnd('/') + recipe.imageUrl
                } else {
                    recipe.imageUrl
                }
                Picasso.get().load(fullUrl).fit().centerCrop()
                    .placeholder(R.color.light_teal)
                    .into(binding.ivRecipeHero)
            }

            updateFavoriteIcon(recipe.isFavorite || recipe.type == RecipeEntity.TYPE_FAVORITE)

            binding.btnFavorite.setOnClickListener {
                if (recipe.type == RecipeEntity.TYPE_FAVORITE) {
                    viewModel.removeFromFavorites(recipe)
                } else {
                    viewModel.toggleFavoriteFromRecommended(recipe)
                }
            }

            populateIngredients(recipe.ingredientsJson)
            populateSteps(recipe.stepsJson)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_star_filled)
            binding.btnFavorite.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFD700"))
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_star_outline)
            binding.btnFavorite.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.dark_teal)
            )
        }
    }

    private fun populateIngredients(json: String) {
        binding.llIngredients.removeAllViews()
        val type = object : TypeToken<List<RecipeIngredientDto>>() {}.type
        val ingredients: List<RecipeIngredientDto> = try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) { emptyList() }

        for (ingredient in ingredients) {
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 12, 0, 12)
            }

            val bullet = TextView(requireContext()).apply {
                text = "•"
                textSize = 16f
                setTextColor(ContextCompat.getColor(context, R.color.teal_primary))
                setPadding(0, 0, 16, 0)
            }

            val text = TextView(requireContext()).apply {
                this.text = "${ingredient.name}  —  ${ingredient.amount}"
                textSize = 15f
                setTextColor(Color.parseColor("#333333"))
            }

            row.addView(bullet)
            row.addView(text)
            binding.llIngredients.addView(row)

            val divider = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
                ).apply { setMargins(0, 0, 0, 0) }
                setBackgroundColor(ContextCompat.getColor(context, R.color.divider_color))
            }
            binding.llIngredients.addView(divider)
        }
    }

    private fun populateSteps(json: String) {
        binding.llSteps.removeAllViews()
        val type = object : TypeToken<List<String>>() {}.type
        val steps: List<String> = try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) { emptyList() }

        for ((index, step) in steps.withIndex()) {
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 16, 0, 16)
            }

            val number = TextView(requireContext()).apply {
                text = "${index + 1}"
                textSize = 14f
                setTextColor(Color.WHITE)
                gravity = android.view.Gravity.CENTER
                val size = (28 * resources.displayMetrics.density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(0, 0, 16, 0)
                }
                background = ContextCompat.getDrawable(context, R.drawable.bg_step_number)
            }

            val text = TextView(requireContext()).apply {
                this.text = step
                textSize = 15f
                setTextColor(Color.parseColor("#333333"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            row.addView(number)
            row.addView(text)
            binding.llSteps.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
