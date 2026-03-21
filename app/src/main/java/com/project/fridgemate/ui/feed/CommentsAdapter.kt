package com.project.fridgemate.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.R
import com.project.fridgemate.databinding.ItemCommentBinding
import android.view.View

class CommentAdapter(
    private val comments: List<Comment>,
    private val onDeleteComment: (Comment) -> Unit = {},
    private val onEditComment: (Comment, String) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        with(holder.binding) {
            tvCommentUserName.text = comment.userName
            tvCommentText.text = comment.text
            ivCommentUserPhoto.setImageResource(R.drawable.ic_person)
            if (comment.isOwner) {
                btnCommentOptions.visibility = View.VISIBLE
                btnCommentOptions.setOnClickListener {
                    showCommentOptions(it, comment)
                }
            } else {
                btnCommentOptions.visibility = View.GONE
            }
        }
    }

    private fun showCommentOptions(anchor: View, comment: Comment) {
        val popup = androidx.appcompat.widget.PopupMenu(anchor.context, anchor)
        popup.menu.add(0, 1, 0, "✏️ Edit")
        popup.menu.add(0, 2, 1, "🗑️ Delete")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    showEditDialog(anchor, comment)
                    true
                }
                2 -> {
                    androidx.appcompat.app.AlertDialog.Builder(anchor.context)
                        .setTitle("Delete Comment?")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Delete") { _, _ ->
                            onDeleteComment(comment)
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

    private fun showEditDialog(anchor: View, comment: Comment) {
        val editText = android.widget.EditText(anchor.context)
        editText.setText(comment.text)

        androidx.appcompat.app.AlertDialog.Builder(anchor.context)
            .setTitle("Edit Comment")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString().trim()
                if (newText.isNotEmpty()) {
                    onEditComment(comment, newText)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    override fun getItemCount() = comments.size
}