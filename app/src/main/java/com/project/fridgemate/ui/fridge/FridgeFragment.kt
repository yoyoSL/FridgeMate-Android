package com.project.fridgemate.ui.fridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val items = mutableListOf<FridgeItem>()
        
        // Mock data
        val lowStockItems = listOf("Milk", "Spinach")
        if (lowStockItems.isNotEmpty()) {
            items.add(FridgeItem.RunningLow(lowStockItems))
        }

        items.add(FridgeItem.CategoryHeader("Protein"))
        items.add(FridgeItem.Product("Chicken Breast", "500g", false))
        items.add(FridgeItem.Product("Eggs", "4 units", false))

        items.add(FridgeItem.CategoryHeader("Dairy"))
        items.add(FridgeItem.Product("Milk", "200ml", true))
        items.add(FridgeItem.Product("Cheddar Cheese", "250g", false))

        items.add(FridgeItem.CategoryHeader("Vegetables"))
        items.add(FridgeItem.Product("Spinach", "100g", true))
        items.add(FridgeItem.Product("Tomatoes", "6 units", false))

        val adapter = FridgeAdapter(items)
        binding.rvFridge.layoutManager = LinearLayoutManager(context)
        binding.rvFridge.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

sealed class FridgeItem {
    data class RunningLow(val ingredients: List<String>) : FridgeItem()
    data class CategoryHeader(val name: String) : FridgeItem()
    data class Product(val name: String, val quantity: String, val isLowStock: Boolean) : FridgeItem()
}
