package com.project.fridgemate.ui.profile

import androidx.fragment.app.activityViewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentDietaryPrefBinding
class DietaryPrefFragment : Fragment() {

    companion object {
        fun newInstance() = DietaryPrefFragment()
    }
    private var _binding: FragmentDietaryPrefBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDietaryPrefBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val current = viewModel.selectedPreference.value
        val idToCheck = when (current) {
            "VEGETARIAN"  -> R.id.rbVegetarian
            "VEGAN"       -> R.id.rbVegan
            "PESCATARIAN" -> R.id.rbPescatarian
            else          -> R.id.rbNone
        }
        binding.radioGroupDietary.check(idToCheck)

        binding.radioGroupDietary.setOnCheckedChangeListener { _, checkedId ->
            val preference = when (checkedId) {
                R.id.rbNone        -> "NONE"
                R.id.rbVegetarian  -> "VEGETARIAN"
                R.id.rbVegan       -> "VEGAN"
                R.id.rbPescatarian -> "PESCATARIAN"
                else               -> "NONE"
            }
            viewModel.onPreferenceSelected(preference)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}