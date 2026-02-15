package com.project.fridgemate.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.fridgemate.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class MyProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }
        setupAllergies(view)
    }
    private fun setupAllergies(view: View) {
        val allergies = listOf(
            AllergyItem("Peanuts"),
            AllergyItem("Tree Nuts"),
            AllergyItem("Dairy"),
            AllergyItem("Eggs"),
            AllergyItem("Soy"),
            AllergyItem("Wheat/Gluten"),
            AllergyItem("Fish"),
            AllergyItem("Shellfish"),
            AllergyItem("Sesame")
        )
        val adapter = AllergyAdapter(allergies)
        view.findViewById<RecyclerView>(R.id.rvAllergies).apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        // val selected = adapter.getSelectedAllergies()
        // TODO: PUT /users/me { "allergies": selected }
    }
}
