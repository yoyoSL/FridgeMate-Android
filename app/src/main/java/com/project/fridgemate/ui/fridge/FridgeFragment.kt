package com.project.fridgemate.ui.fridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.fridgemate.R
import com.project.fridgemate.data.remote.ApiClient
import com.project.fridgemate.databinding.FragmentFridgeBinding

class FridgeFragment : Fragment() {

    private var _binding: FragmentFridgeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFridgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showEmptyState()
    }

    private fun showEmptyState() {
        binding.rvFridge.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE

        if (!ApiClient.getTokenManager().isLoggedIn) {
            binding.tvEmptyTitle.text = getString(R.string.fridge_not_logged_in_title)
            binding.tvEmptyDesc.text = getString(R.string.fridge_not_logged_in_desc)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
