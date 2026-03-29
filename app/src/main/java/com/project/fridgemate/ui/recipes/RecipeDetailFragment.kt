package com.project.fridgemate.ui.recipes

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.fridgemate.BuildConfig
import com.project.fridgemate.R
import com.project.fridgemate.data.local.AppDatabase
import com.project.fridgemate.data.local.entity.RecipeEntity
import com.project.fridgemate.databinding.ItemDetailIngredientBinding
import com.project.fridgemate.databinding.ItemDetailStepBinding
import com.project.fridgemate.data.remote.dto.RecipeIngredientDto
import com.project.fridgemate.data.repository.RecipeRepository
import com.project.fridgemate.databinding.FragmentRecipeDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private val viewModel: RecipesViewModel by activityViewModels()
    private val args: RecipeDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        val recipeId = args.recipeId
        val serverRecipeId = args.serverRecipeId

        val dao = AppDatabase.getInstance(requireContext()).recipeDao()

        if (serverRecipeId.isNotEmpty()) {
            binding.loadingOverlay.visibility = View.VISIBLE
            binding.contentScroll.visibility = View.GONE

            val repository = RecipeRepository(dao)
            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    repository.fetchAndCacheRecipeByServerId(serverRecipeId)
                }
                if (result.isFailure) {
                    Toast.makeText(requireContext(), "Could not load recipe", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    return@launch
                }
            }
            dao.getByServerId(serverRecipeId).observe(viewLifecycleOwner) { recipe ->
                if (recipe == null) return@observe
                binding.loadingOverlay.visibility = View.GONE
                binding.contentScroll.visibility = View.VISIBLE
                bindRecipe(recipe)
            }
        } else if (recipeId != 0L) {
            dao.getById(recipeId).observe(viewLifecycleOwner) { recipe ->
                if (recipe == null) return@observe
                bindRecipe(recipe)
            }
        } else {
            findNavController().navigateUp()
        }
    }

    private fun bindRecipe(recipe: RecipeEntity) {
        binding.tvTitle.text = recipe.title
        binding.tvDescription.text = recipe.description.ifBlank { "A delicious recipe just for you." }
        binding.tvRecipeTime.text = recipe.cookingTime.ifBlank { "—" }
        binding.tvRecipeDifficulty.text = recipe.difficulty

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

        updateFavoriteIcon(recipe.isFavorite)

        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite(recipe)
        }

        if (recipe.serverId != null) {
            binding.btnShareAsPost.visibility = View.VISIBLE
            binding.btnShareAsPost.setOnClickListener {
                val action = RecipeDetailFragmentDirections
                    .actionRecipeDetailFragmentToAddPostFragment(
                        prefillTitle = "",
                        prefillDescription = "\uD83C\uDF73 I made \"${recipe.title}\"! Check out this recipe:",
                        prefillRecipeId = recipe.serverId ?: "",
                        prefillRecipeName = recipe.title,
                        prefillRecipeTime = recipe.cookingTime,
                        prefillRecipeDifficulty = recipe.difficulty
                    )
                findNavController().navigate(action)
            }
        } else {
            binding.btnShareAsPost.visibility = View.GONE
        }

        populateIngredients(recipe.ingredientsJson)
        populateSteps(recipe.stepsJson)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_star_filled)
            binding.btnFavorite.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFD700"))
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_star_outline)
            binding.btnFavorite.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.gray_text)
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
            val itemBinding = ItemDetailIngredientBinding.inflate(layoutInflater, binding.llIngredients, false)
            itemBinding.tvIngredientText.text = if (ingredient.amount.isNotBlank()) {
                "${ingredient.name}  \u2014  ${ingredient.amount}"
            } else {
                ingredient.name
            }
            binding.llIngredients.addView(itemBinding.root)
        }
    }

    private fun populateSteps(json: String) {
        binding.llSteps.removeAllViews()
        val type = object : TypeToken<List<String>>() {}.type
        val steps: List<String> = try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) { emptyList() }

        for ((index, step) in steps.withIndex()) {
            val itemBinding = ItemDetailStepBinding.inflate(layoutInflater, binding.llSteps, false)
            itemBinding.tvStepNumber.text = (index + 1).toString()
            itemBinding.tvStepText.text = step
            binding.llSteps.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
