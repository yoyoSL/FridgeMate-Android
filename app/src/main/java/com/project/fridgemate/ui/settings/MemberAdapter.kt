package com.project.fridgemate.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.BuildConfig
import com.project.fridgemate.R
import com.project.fridgemate.data.remote.dto.FridgeMemberDetailDto
import com.project.fridgemate.databinding.ItemMemberBinding
import com.squareup.picasso.Picasso

class MemberAdapter(
    private val members: List<FridgeMemberDetailDto>,
    private val currentUserId: String? = null
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
        val isCurrentUser = member.userId == currentUserId
        with(holder.binding) {
            tvMemberName.text = if (isCurrentUser) "${member.displayName} (You)"
            else member.displayName
            val profileImage = member.profileImage
            if (!profileImage.isNullOrEmpty()) {
                val url = if (profileImage.startsWith("/"))
                    BuildConfig.BASE_URL.trimEnd('/') + profileImage
                else profileImage
                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivMemberPhoto)
            } else {
                ivMemberPhoto.setImageResource(R.drawable.ic_person)
            }
        }
    }

    override fun getItemCount() = members.size
}
