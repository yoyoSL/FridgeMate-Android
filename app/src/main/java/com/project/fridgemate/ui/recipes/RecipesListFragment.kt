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

        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())

        when (type) {
            TYPE_RECOMMENDED -> {
                viewModel.recommended.observe(viewLifecycleOwner) { recipes ->
                    binding.rvRecipes.adapter = RecipeAdapter(recipes) { recipe ->
                        viewModel.toggleFavorite(recipe)
                    }
                }
            }
            TYPE_FAVORITES -> {
                viewModel.favorites.observe(viewLifecycleOwner) { recipes ->
                    binding.rvRecipes.adapter = RecipeAdapter(recipes) { recipe ->
                        viewModel.toggleFavorite(recipe)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}