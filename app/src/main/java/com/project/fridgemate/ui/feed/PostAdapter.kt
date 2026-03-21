package com.project.fridgemate.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.R
import com.project.fridgemate.databinding.ItemPostBinding
import com.squareup.picasso.Picasso

class PostAdapter(
    posts: List<Post>,
    private val onLikeClick: (Post) -> Unit,
    private val onAddComment: (postId: String, text: String) -> Unit,
    private val onDeleteClick: (Post) -> Unit,
    private val onEditClick: (Post) -> Unit,
    private val onDeleteComment: (postId: String, commentId: String) -> Unit,
    private val onEditComment: (postId: String, commentId: String, newText: String) -> Unit,
    private val onExpandComments: (postId: String) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val posts: MutableList<Post> = posts.toMutableList()

    private val expandedPosts = mutableSetOf<String>()

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
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
        val post = posts[position]
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

            tvLikesCount.text = post.likesCount.toString()
            updateLikeButton(this, post.isLiked)

            tvCommentsCount.text = post.commentsCount.toString()

            btnLike.setOnClickListener { onLikeClick(post) }
            btnComment.setOnClickListener {
                if (rvComments.visibility == View.GONE) {
                    expandedPosts.add(post.id)
                    rvComments.visibility = View.VISIBLE
                    layoutAddComment.visibility = View.VISIBLE
                    onExpandComments(post.id)
                    rvComments.layoutManager = LinearLayoutManager(holder.itemView.context)
                    rvComments.adapter = CommentAdapter(
                        comments = posts[holder.adapterPosition].comments,
                        onDeleteComment = { comment -> onDeleteComment(post.id, comment.id) },
                        onEditComment = { comment, newText -> onEditComment(post.id, comment.id, newText) }
                    )
                } else {
                    expandedPosts.remove(post.id)
                    rvComments.visibility = View.GONE
                    layoutAddComment.visibility = View.GONE
                }
            }

            if (expandedPosts.contains(post.id)) {
                rvComments.visibility = View.VISIBLE
                layoutAddComment.visibility = View.VISIBLE
                rvComments.layoutManager = LinearLayoutManager(holder.itemView.context)
                rvComments.adapter = CommentAdapter(
                    comments = post.comments,
                    onDeleteComment = { comment -> onDeleteComment(post.id, comment.id) },
                    onEditComment = { comment, newText -> onEditComment(post.id, comment.id, newText) }
                )
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

    private fun showOptionsMenu(anchor: View, post: Post) {
        val popup = androidx.appcompat.widget.PopupMenu(anchor.context, anchor)
        popup.menu.add(0, 1, 0, "✏️ Edit")
        popup.menu.add(0, 2, 1, "🗑️ Delete")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    onEditClick(post)
                    true
                }
                2 -> {
                    androidx.appcompat.app.AlertDialog.Builder(anchor.context)
                        .setTitle("Delete Post?")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("Delete") { _, _ ->
                            onDeleteClick(post)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                    true
                }
                else -> false
            }
        }
        popup.show()
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
