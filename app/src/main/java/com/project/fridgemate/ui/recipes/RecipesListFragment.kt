package com.project.fridgemate.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.fridgemate.databinding.FragmentRecipeListBinding

class RecipeListFragment : Fragment() {

    companion object {
        private const val ARG_TYPE = "type"
        const val TYPE_RECOMMENDED = "RECOMMENDED"
        const val TYPE_FAVORITES = "FAVORITES"

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
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                binding.btnGenerate.isEnabled = !loading
            }

            binding.btnGenerate.setOnClickListener {
                viewModel.loadRecommended()
            }

            if (viewModel.recommended.value.isNullOrEmpty()) {
                viewModel.loadRecommended()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
