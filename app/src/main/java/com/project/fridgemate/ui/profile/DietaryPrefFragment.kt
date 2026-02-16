package com.project.fridgemate.ui.profile

import androidx.fragment.app.activityViewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.fridgemate.R
import android.widget.RadioGroup
class DietaryPrefFragment : Fragment() {

    companion object {
        fun newInstance() = DietaryPrefFragment()
    }
    private val viewModel: DietaryPrefViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dietary_pref, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupDietary)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
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

}