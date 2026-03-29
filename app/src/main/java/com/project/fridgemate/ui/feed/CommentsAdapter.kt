package com.project.fridgemate.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.BuildConfig
import com.project.fridgemate.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.project.fridgemate.databinding.DialogCommentOptionsBinding
import com.project.fridgemate.databinding.DialogConfirmDeleteBinding
import com.project.fridgemate.databinding.ItemCommentBinding
import com.squareup.picasso.Picasso

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
            if (comment.authorImageUrl.isNotEmpty()) {
                val url = if (comment.authorImageUrl.startsWith("/"))
                    BuildConfig.BASE_URL.trimEnd('/') + comment.authorImageUrl
                else comment.authorImageUrl
                Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivCommentUserPhoto)
            } else {
                ivCommentUserPhoto.setImageResource(R.drawable.ic_person)
            }
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
        val context = anchor.context
        val dialog = BottomSheetDialog(context)
        val binding = DialogCommentOptionsBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.btnEdit.setOnClickListener {
            dialog.dismiss()
            showEditDialog(anchor, comment)
        }

        binding.btnDelete.setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmation(context, comment)
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(context: android.content.Context, comment: Comment) {
        val dialog = BottomSheetDialog(context)
        val binding = DialogConfirmDeleteBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.tvTitle.text = context.getString(R.string.delete_comment)
        binding.tvMessage.text = context.getString(R.string.delete_comment_confirmation)

        binding.btnConfirmDelete.setOnClickListener {
            onDeleteComment(comment)
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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