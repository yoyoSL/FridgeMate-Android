package com.project.fridgemate.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.project.fridgemate.MainActivity
import com.project.fridgemate.R
import com.project.fridgemate.data.local.AppDatabase
import com.project.fridgemate.data.repository.AuthRepository
import com.project.fridgemate.databinding.FragmentDashboardBinding
import com.project.fridgemate.databinding.PopupProfileMenuBinding
import com.project.fridgemate.ui.fridge.FridgeFragment
import com.project.fridgemate.ui.profile.ProfileViewModel
import com.project.fridgemate.ui.recipes.RecipesFragment
import com.project.fridgemate.ui.feed.FeedFragment

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val authRepository = AuthRepository()
    private val profileViewModel: ProfileViewModel by viewModels()

    private var currentTabId: Int = R.id.tab_feed

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

        if (savedInstanceState != null) {
            currentTabId = savedInstanceState.getInt("selected_tab_id", R.id.tab_feed)
        }

        // Restore tab selection UI
        val selectedTabView = when (currentTabId) {
            R.id.tab_feed -> binding.tabFeed
            R.id.tab_recipes -> binding.tabRecipes
            else -> binding.tabMyFridge
        }
        selectTab(selectedTabView)

        // Only set default fragment if it's the first time
        if (savedInstanceState == null) {
            showFragment(FeedFragment())
        }

        setupTabListeners()
        setupProfileMenu()
        loadGreeting()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selected_tab_id", currentTabId)
    }

    private fun loadGreeting() {
        profileViewModel.loadProfile()
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                val firstName = it.displayName.split(" ").firstOrNull() ?: it.displayName
                binding.tvGreeting.text = getString(R.string.greeting_format, firstName)
            }
        }
    }

    private fun setupTabListeners() {
        binding.tabMyFridge.setOnClickListener {
            if (currentTabId != it.id) {
                currentTabId = it.id
                selectTab(it)
                showFragment(FridgeFragment())
            }
        }
        binding.tabFeed.setOnClickListener { 
            if (currentTabId != it.id) {
                currentTabId = it.id
                selectTab(it)
                showFragment(FeedFragment())
            }
        }
        binding.tabRecipes.setOnClickListener { 
            if (currentTabId != it.id) {
                currentTabId = it.id
                selectTab(it)
                showFragment(RecipesFragment())
            }
        }
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
            findNavController().navigate(R.id.action_dashboardFragment_to_settingsFragment)
        }

        popupBinding.menuLogout.setOnClickListener {
            popupWindow.dismiss()
            lifecycleScope.launch {
                authRepository.logout()
                withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(requireContext()).clearAllTables()
                }
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
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
        resetTab(binding.tabFeed, binding.ivTabFeed, binding.tvTabFeed)
        resetTab(binding.tabMyFridge, binding.ivTabFridge, binding.tvTabFridge)
        resetTab(binding.tabRecipes, binding.ivTabRecipes, binding.tvTabRecipes)

        // Highlight selected tab
        val accentColor = ContextCompat.getColor(requireContext(), R.color.teal_primary)

        when (selectedTabView.id) {
            R.id.tab_my_fridge -> {
                binding.ivTabFridge.setColorFilter(accentColor)
                binding.tvTabFridge.setTextColor(accentColor)
            }
            R.id.tab_recipes -> {
                binding.ivTabRecipes.setColorFilter(accentColor)
                binding.tvTabRecipes.setTextColor(accentColor)
            }
            R.id.tab_feed -> {
                binding.ivTabFeed.setColorFilter(accentColor)
                binding.tvTabFeed.setTextColor(accentColor)
            }
        }
    }

    private fun resetTab(tab: View, icon: android.widget.ImageView, text: android.widget.TextView) {
        val gray = ContextCompat.getColor(requireContext(), R.color.gray_text)
        icon.setColorFilter(gray)
        text.setTextColor(gray)
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