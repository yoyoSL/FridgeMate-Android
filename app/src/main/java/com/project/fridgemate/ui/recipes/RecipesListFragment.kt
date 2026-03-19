package com.project.fridgemate.ui.recipes

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.fridgemate.databinding.FragmentRecipeListBinding

class RecipeListFragment : Fragment() {

    companion object {
        private const val ARG_TYPE = "type"
        const val TYPE_RECOMMENDED = "RECOMMENDED"
        const val TYPE_FAVORITES = "FAVORITES"

        private val COOKING_TIPS = listOf(
            "A pinch of salt can enhance sweetness in desserts",
            "Let meat rest after cooking for juicier results",
            "Fresh herbs should be added at the end of cooking",
            "Room temperature eggs blend better in batter",
            "Toast your spices to unlock deeper flavors",
            "Always preheat your pan before adding oil",
            "Acid like lemon juice brightens any dish",
            "Pat proteins dry for a better sear",
            "Use a sharp knife — it's safer than a dull one",
            "Season every layer as you cook, not just at the end"
        )

        fun newInstance(type: String): RecipeListFragment {
            return RecipeListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TYPE, type)
                }
            }
        }
    }

    private var _binding: FragmentRecipeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipesViewModel by activityViewModels()
    private lateinit var adapter: RecipeAdapter

    private val tipHandler = Handler(Looper.getMainLooper())
    private var tipIndex = 0
    private val tipRunnable = object : Runnable {
        override fun run() {
            if (_binding == null) return
            tipIndex = (tipIndex + 1) % COOKING_TIPS.size
            val fadeOut = AlphaAnimation(1f, 0f).apply { duration = 300 }
            val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 300 }
            binding.tvLoadingTip.startAnimation(fadeOut)
            tipHandler.postDelayed({
                if (_binding == null) return@postDelayed
                binding.tvLoadingTip.text = COOKING_TIPS[tipIndex]
                binding.tvLoadingTip.startAnimation(fadeIn)
            }, 300)
            tipHandler.postDelayed(this, 4000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getString(ARG_TYPE) ?: TYPE_RECOMMENDED

        val onFavoriteClick = when (type) {
            TYPE_RECOMMENDED -> { recipe: com.project.fridgemate.data.local.entity.RecipeEntity ->
                viewModel.toggleFavoriteFromRecommended(recipe)
            }
            else -> { recipe: com.project.fridgemate.data.local.entity.RecipeEntity ->
                viewModel.removeFromFavorites(recipe)
            }
        }

        adapter = RecipeAdapter(onFavoriteClick)
        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecipes.adapter = adapter

        val source = when (type) {
            TYPE_RECOMMENDED -> viewModel.recommended
            else -> viewModel.favorites
        }

        source.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
            val isEmpty = recipes.isEmpty() && viewModel.isLoading.value != true
            binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }

        if (type == TYPE_RECOMMENDED) {
            binding.btnGenerate.visibility = View.VISIBLE

            viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
                if (loading) showLoading() else hideLoading()
                binding.btnGenerate.isEnabled = !loading
            }

            viewModel.error.observe(viewLifecycleOwner) { error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
            }

            binding.btnGenerate.setOnClickListener {
                viewModel.loadRecommended()
            }

            viewModel.loadRecommendedIfNeeded()
        }
    }

    private fun showLoading() {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.rvRecipes.visibility = View.GONE
        binding.btnGenerate.visibility = View.GONE
        tipIndex = (0 until COOKING_TIPS.size).random()
        binding.tvLoadingTip.text = COOKING_TIPS[tipIndex]
        tipHandler.postDelayed(tipRunnable, 4000)
    }

    private fun hideLoading() {
        tipHandler.removeCallbacks(tipRunnable)
        binding.loadingOverlay.visibility = View.GONE
        binding.rvRecipes.visibility = View.VISIBLE
        binding.btnGenerate.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        tipHandler.removeCallbacks(tipRunnable)
        super.onDestroyView()
        _binding = null
    }
}
