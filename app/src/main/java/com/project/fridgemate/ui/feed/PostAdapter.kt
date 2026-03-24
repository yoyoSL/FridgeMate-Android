package com.project.fridgemate.ui.feed

import android.content.res.ColorStateList
import android.graphics.Color
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.project.fridgemate.R
import com.project.fridgemate.databinding.DialogPostOptionsBinding
import com.project.fridgemate.databinding.ItemPostBinding
import com.squareup.picasso.Picasso

class PostAdapter(
    private val onLikeClick: (Post) -> Unit,
    private val onAddComment: (postId: String, text: String) -> Unit,
    private val onDeleteClick: (Post) -> Unit,
    private val onEditClick: (Post) -> Unit,
    private val onDeleteComment: (postId: String, commentId: String) -> Unit,
    private val onEditComment: (postId: String, commentId: String, newText: String) -> Unit,
    private val onExpandComments: (postId: String) -> Unit,
    private val onRecipeClick: (LinkedRecipe) -> Unit = {}
) : ListAdapter<Post, PostAdapter.PostViewHolder>(DIFF_CALLBACK) {

    private val expandedPosts = mutableSetOf<String>()

    companion object {
        private const val PAYLOAD_LIKE = "PAYLOAD_LIKE"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem

            override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
                return if (oldItem.isLiked != newItem.isLiked || oldItem.likesCount != newItem.likesCount) {
                    PAYLOAD_LIKE
                } else {
                    null
                }
            }
        }
    }

    inner class PostViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        with(holder.binding) {

            tvUserName.text = post.userName
            tvUserLocation.text = post.userLocation
            ivUserPhoto.setImageResource(R.drawable.ic_person)

            if (post.imageUrl.isNotEmpty()) {
                ivPostImage.visibility = View.VISIBLE
                ivPostImage.setBackgroundColor(0)
                Picasso.get()
                    .load(post.imageUrl)
                    .placeholder(R.color.light_teal)
                    .error(R.color.light_teal)
                    .into(ivPostImage)
            } else {
                ivPostImage.visibility = View.GONE
            }

            tvRecipeTitle.text = post.postTitle
            tvDescription.text = post.description

            val recipe = post.linkedRecipe
            if (recipe != null) {
                cardLinkedRecipe.visibility = View.VISIBLE
                tvLinkedRecipeTitle.text = recipe.title
                val info = buildString {
                    if (recipe.cookingTime.isNotBlank()) append(recipe.cookingTime)
                    if (recipe.difficulty.isNotBlank()) {
                        if (isNotEmpty()) append(" · ")
                        append(recipe.difficulty)
                    }
                }
                tvLinkedRecipeInfo.text = info
                if (recipe.imageUrl.isNotEmpty()) {
                    Picasso.get()
                        .load(recipe.imageUrl)
                        .placeholder(R.color.light_teal)
                        .error(R.color.light_teal)
                        .into(ivRecipeThumb)
                }
                cardLinkedRecipe.setOnClickListener { onRecipeClick(recipe) }
            } else {
                cardLinkedRecipe.visibility = View.GONE
            }

            tvLikesCount.text = post.likesCount.toString()
            updateLikeButton(btnLike, post.isLiked, animate = false)

            tvCommentsCount.text = post.commentsCount.toString()

            btnLike.setOnClickListener { 
                val currentPos = holder.adapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    onLikeClick(getItem(currentPos))
                }
            }
            
            btnComment.setOnClickListener {
                // Use the RecyclerView as the transition root to prevent cards from colliding/jumping
                val recyclerView = root.parent as? RecyclerView
                if (recyclerView != null) {
                    TransitionManager.beginDelayedTransition(recyclerView, AutoTransition().apply {
                        duration = 120
                    })
                } else {
                    TransitionManager.beginDelayedTransition(root as ViewGroup, AutoTransition().apply {
                        duration = 120
                    })
                }

                if (rvComments.visibility == View.GONE) {
                    expandedPosts.add(post.id)
                    rvComments.visibility = View.VISIBLE
                    layoutAddComment.visibility = View.VISIBLE
                    onExpandComments(post.id)
                    setupComments(holder, post)
                } else {
                    expandedPosts.remove(post.id)
                    rvComments.visibility = View.GONE
                    layoutAddComment.visibility = View.GONE
                }
            }

            if (expandedPosts.contains(post.id)) {
                rvComments.visibility = View.VISIBLE
                layoutAddComment.visibility = View.VISIBLE
                setupComments(holder, post)
            } else {
                rvComments.visibility = View.GONE
                layoutAddComment.visibility = View.GONE
            }

            btnSendComment.setOnClickListener {
                val text = etComment.text.toString().trim()
                if (text.isNotEmpty()) {
                    onAddComment(post.id, text)
                    etComment.text?.clear()
                }
            }
            if (post.isOwner) {
                btnMoreOptions.visibility = View.VISIBLE
                btnMoreOptions.setOnClickListener {
                    showOptionsMenu(it, post)
                }
            } else {
                btnMoreOptions.visibility = View.GONE
            }
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_LIKE)) {
            val post = getItem(position)
            holder.binding.tvLikesCount.text = post.likesCount.toString()
            updateLikeButton(holder.binding.btnLike, post.isLiked, animate = true)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    private fun setupComments(holder: PostViewHolder, post: Post) {
        holder.binding.rvComments.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.binding.rvComments.adapter = CommentAdapter(
            comments = post.comments,
            onDeleteComment = { comment -> onDeleteComment(post.id, comment.id) },
            onEditComment = { comment, newText -> onEditComment(post.id, comment.id, newText) }
        )
    }

    private fun showOptionsMenu(anchor: View, post: Post) {
        val context = anchor.context
        val dialog = BottomSheetDialog(context)
        val binding = DialogPostOptionsBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.btnEdit.setOnClickListener {
            onEditClick(post)
            dialog.dismiss()
        }

        binding.btnDelete.setOnClickListener {
            dialog.dismiss()
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete_post))
                .setMessage(context.getString(R.string.delete_post_confirmation))
                .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                    onDeleteClick(post)
                }
                .setNegativeButton(context.getString(R.string.cancel), null)
                .show()
        }

        dialog.show()
    }

    private fun updateLikeButton(btn: ImageButton, isLiked: Boolean, animate: Boolean) {
        if (isLiked) {
            btn.setImageResource(R.drawable.ic_heart_filled)
            btn.imageTintList = ColorStateList.valueOf(Color.parseColor("#E53935"))
            if (animate) viewScalePop(btn, 1.2f)
        } else {
            btn.setImageResource(R.drawable.ic_heart_outline)
            btn.imageTintList = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
            if (animate) viewScalePop(btn, 0.9f)
        }
    }

    private fun viewScalePop(view: ImageButton, scale: Float) {
        view.animate().cancel()
        view.scaleX = 0.8f
        view.scaleY = 0.8f
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(200)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
}
