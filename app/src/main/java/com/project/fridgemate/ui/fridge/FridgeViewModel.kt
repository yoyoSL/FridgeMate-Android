package com.project.fridgemate.ui.fridge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fridgemate.data.remote.dto.InventoryItemDto
import com.project.fridgemate.data.repository.FridgeRepository
import com.project.fridgemate.data.repository.FridgeResult
import com.project.fridgemate.data.repository.InventoryItemRepository
import kotlinx.coroutines.launch

class FridgeViewModel : ViewModel() {

    private val fridgeRepository = FridgeRepository()
    private val itemRepository = InventoryItemRepository()

    sealed class State {
        object Loading : State()
        data class Items(val items: List<FridgeItem>) : State()
        object Empty : State()
        object NoFridge : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    fun loadItems() {
        viewModelScope.launch {
            _state.value = State.Loading
            when (val fridgeResult = fridgeRepository.getMyFridge()) {
                is FridgeResult.NoFridge -> _state.value = State.NoFridge
                is FridgeResult.Error -> _state.value = State.Error(fridgeResult.message)
                is FridgeResult.Success -> {
                    val fridgeId = fridgeResult.data.id
                    val items = itemRepository.getItems(fridgeId)
                    _state.value = if (items.isEmpty()) {
                        State.Empty
                    } else {
                        State.Items(buildFridgeItemList(items))
                    }
                }
            }
        }
    }

    private fun buildFridgeItemList(items: List<InventoryItemDto>): List<FridgeItem> {
        val result = mutableListOf<FridgeItem>()
        val lowItems = items.filter { it.isRunningLow }
        if (lowItems.isNotEmpty()) {
            result.add(FridgeItem.RunningLow(lowItems.map { it.name }))
        }
        result.add(FridgeItem.CategoryHeader("All Items"))
        items.forEach { result.add(FridgeItem.Product(it.name, it.quantity, it.isRunningLow)) }
        return result
    }
}
