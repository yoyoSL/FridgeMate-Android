package com.project.fridgemate.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentDashboardBinding
import com.project.fridgemate.databinding.PopupProfileMenuBinding
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
        setupProfileMenu()
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

    private fun setupProfileMenu() {
        binding.ivProfile.setOnClickListener {
            showProfilePopup(it)
        }
    }

    private fun showProfilePopup(anchor: View) {
        val popupBinding = PopupProfileMenuBinding.inflate(layoutInflater)
        
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupBinding.menuProfile.setOnClickListener {
            popupWindow.dismiss()
            findNavController().navigate(R.id.action_dashboardFragment_to_myProfileFragment)
        }

        popupBinding.menuSettings.setOnClickListener {
            popupWindow.dismiss()
        }

        popupBinding.menuLogout.setOnClickListener {
            popupWindow.dismiss()
            findNavController().navigate(R.id.action_dashboardFragment_to_authFragment)
        }

        popupWindow.elevation = 8f

        // Measure the popup to calculate offset
        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = popupBinding.root.measuredWidth
        
        // Calculate xOffset to align the right edge of the popup with the right edge of the anchor
        val xOffset = anchor.width - popupWidth
        val yOffset = resources.getDimensionPixelSize(R.dimen.margin_small)
        
        popupWindow.showAsDropDown(anchor, xOffset, yOffset)
    }

    private fun selectTab(selectedTabView: View) {
        resetTab(binding.tabFeed, binding.ivTabFeed, binding.tvTabFeed, null)
        resetTab(binding.tabMyFridge, binding.ivTabFridge, binding.tvTabFridge, binding.vIndicatorFridge)
        resetTab(binding.tabRecipes, binding.ivTabRecipes, binding.tvTabRecipes, null)
        resetTab(binding.tabJournal, binding.ivTabJournal, binding.tvTabJournal, null)

        // Highlight selected tab
        val accentColor = ContextCompat.getColor(requireContext(), R.color.teal_primary)
        
        when (selectedTabView.id) {
            R.id.tab_my_fridge -> {
                binding.ivTabFridge.setColorFilter(accentColor)
                binding.tvTabFridge.setTextColor(accentColor)
                binding.vIndicatorFridge.setBackgroundColor(accentColor)
                binding.vIndicatorFridge.visibility = View.VISIBLE
            }
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
