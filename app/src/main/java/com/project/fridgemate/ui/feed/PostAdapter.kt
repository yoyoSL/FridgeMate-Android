package com.project.fridgemate.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.R
import com.project.fridgemate.databinding.ItemPostBinding

class PostAdapter(
    private val posts: List<Post>,
    private val onLikeClick: (Post) -> Unit,
    private val onFavoriteClick: (Post) -> Unit,
    private val onCommentClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {

            // user details
            tvUserName.text = post.userName
            tvUserLocation.text = post.userLocation
            ivUserPhoto.setImageResource(R.drawable.ic_person)

            // post pic
            ivPostImage.setImageResource(android.R.color.transparent)
            ivPostImage.setBackgroundColor(
                holder.itemView.context.getColor(R.color.light_teal)
            )

            // description
            tvRecipeTitle.text = post.postTitle
            tvDescription.text = post.description

            // likes
            tvLikesCount.text = post.likesCount.toString()
            updateLikeButton(this, post.isLiked)

            // comments
            tvCommentsCount.text = post.commentsCount.toString()

            // click listeners
            btnLike.setOnClickListener { onLikeClick(post) }
            btnFavorite.setOnClickListener { onFavoriteClick(post) }
            btnComment.setOnClickListener { onCommentClick(post) }
        }
    }

    private fun updateLikeButton(binding: ItemPostBinding, isLiked: Boolean) {
        if (isLiked) {
            binding.btnLike.setImageResource(R.drawable.ic_heart_filled)
            binding.btnLike.imageTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#E53935")
                )
        } else {
            binding.btnLike.setImageResource(R.drawable.ic_heart_outline)
            binding.btnLike.imageTintList =
                android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#9E9E9E")
                )
        }
    }


    override fun getItemCount() = posts.size
}