package com.project.fridgemate.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.R
import com.project.fridgemate.databinding.ItemMemberBinding

data class Member(
    val name: String,
    val isCurrentUser: Boolean = false
)

class MemberAdapter(
    private val members: List<Member>
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(val binding: ItemMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        with(holder.binding) {
            tvMemberName.text = if (member.isCurrentUser) "${member.name} (You)"
            else member.name
            ivMemberPhoto.setImageResource(R.drawable.ic_person)
        }
    }

    override fun getItemCount() = members.size
}