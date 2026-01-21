package com.project.fridgemate.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentDashboardBinding
import com.project.fridgemate.ui.fridge.FridgeFragment

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set My Fridge as default
        selectTab(binding.tabMyFridge)
        showFragment(FridgeFragment())

        setupTabListeners()
    }

    private fun setupTabListeners() {
        binding.tabMyFridge.setOnClickListener {
            selectTab(it)
            showFragment(FridgeFragment())
        }

        binding.tabFeed.setOnClickListener { /* TODO: Implement Feed */ }
        binding.tabRecipes.setOnClickListener { /* TODO: Implement Recipes */ }
        binding.tabJournal.setOnClickListener { /* TODO: Implement Journal */ }
    }

    private fun selectTab(selectedTabView: View) {
        // Reset all tabs
        resetTab(binding.tabFeed, binding.ivTabFeed, binding.tvTabFeed, null)
        resetTab(binding.tabMyFridge, binding.ivTabFridge, binding.tvTabFridge, binding.vIndicatorFridge)
        resetTab(binding.tabRecipes, binding.ivTabRecipes, binding.tvTabRecipes, null)
        resetTab(binding.tabJournal, binding.ivTabJournal, binding.tvTabJournal, null)

        // Highlight selected tab
        val accentColor = ContextCompat.getColor(requireContext(), R.color.accent_green)
        
        when (selectedTabView.id) {
            R.id.tab_my_fridge -> {
                binding.ivTabFridge.setColorFilter(accentColor)
                binding.tvTabFridge.setTextColor(accentColor)
                binding.vIndicatorFridge.setBackgroundColor(accentColor)
                binding.vIndicatorFridge.visibility = View.VISIBLE
            }
            // Add other tabs here when implemented
        }
    }

    private fun resetTab(tab: View, icon: android.widget.ImageView, text: android.widget.TextView, indicator: View?) {
        val gray = ContextCompat.getColor(requireContext(), R.color.gray_text)
        icon.setColorFilter(gray)
        text.setTextColor(gray)
        indicator?.visibility = View.GONE
    }

    private fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.dashboard_nav_host, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
