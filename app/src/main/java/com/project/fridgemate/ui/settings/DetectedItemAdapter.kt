package com.project.fridgemate.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.data.remote.dto.DetectedItemDto
import com.project.fridgemate.databinding.ItemDetectedBinding

class DetectedItemAdapter(
    private val items: List<DetectedItemDto>
) : RecyclerView.Adapter<DetectedItemAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDetectedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetectedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvItemName.text = item.name
        holder.binding.tvItemQuantity.text = item.quantity
    }

    override fun getItemCount() = items.size
}
