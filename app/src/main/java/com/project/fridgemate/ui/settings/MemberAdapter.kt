package com.project.fridgemate.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.project.fridgemate.R

data class Member(
    val name: String,
    val isCurrentUser: Boolean = false
)
class MemberAdapter(
    private val members: List<Member>
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photo: ShapeableImageView = view.findViewById(R.id.ivMemberPhoto)
        val name: TextView = view.findViewById(R.id.tvMemberName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]

        holder.name.text = if (member.isCurrentUser) "${member.name} (You)"
        else member.name

        //TODO ROOM API..
        holder.photo.setImageResource(R.drawable.ic_person)
    }

    override fun getItemCount() = members.size
}