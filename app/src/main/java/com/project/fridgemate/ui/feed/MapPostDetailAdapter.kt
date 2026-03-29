package com.project.fridgemate.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.BuildConfig
import com.project.fridgemate.R
import com.project.fridgemate.databinding.ItemMapPostDetailBinding
import com.squareup.picasso.Picasso

class MapPostDetailAdapter(
    private val posts: List<Post>,
    private val onRecipeClick: (LinkedRecipe) -> Unit
) :
    RecyclerView.Adapter<MapPostDetailAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMapPostDetailBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMapPostDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            tvUserName.text = post.userName
            tvLocation.text = post.userLocation
            tvPostTitle.text = post.postTitle
            tvDescription.text = post.description
            tvLikes.text = "${post.likesCount} likes"
            tvComments.text = "${post.commentsCount} comments"

            if (post.authorImageUrl.isNotEmpty()) {
                val avatarUrl = if (post.authorImageUrl.startsWith("/"))
                    BuildConfig.BASE_URL.trimEnd('/') + post.authorImageUrl
                else post.authorImageUrl
                Picasso.get()
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivUserAvatar)
            } else {
                ivUserAvatar.setImageResource(R.drawable.ic_person)
            }

            if (post.imageUrl.isNotEmpty()) {
                ivPostImage.visibility = View.VISIBLE
                val fullUrl = if (post.imageUrl.startsWith("/")) {
                    BuildConfig.BASE_URL.trimEnd('/') + post.imageUrl
                } else {
                    post.imageUrl
                }
                Picasso.get()
                    .load(fullUrl)
                    .placeholder(R.color.light_teal)
                    .error(R.color.light_teal)
                    .into(ivPostImage)
            } else {
                ivPostImage.visibility = View.GONE
            }

            val recipe = post.linkedRecipe
            if (recipe != null) {
                cardLinkedRecipe.visibility = View.VISIBLE
                tvLinkedRecipeTitle.text = recipe.title
                tvLinkedRecipeInfo.text = "${recipe.cookingTime} · ${recipe.difficulty}"

                if (recipe.imageUrl.isNotEmpty()) {
                    val fullRecipeUrl = if (recipe.imageUrl.startsWith("/")) {
                        BuildConfig.BASE_URL.trimEnd('/') + recipe.imageUrl
                    } else {
                        recipe.imageUrl
                    }
                    Picasso.get()
                        .load(fullRecipeUrl)
                        .placeholder(R.color.teal_primary)
                        .into(ivRecipeThumb)
                } else {
                    ivRecipeThumb.setImageResource(R.color.teal_primary)
                }

                cardLinkedRecipe.setOnClickListener {
                    onRecipeClick(recipe)
                }
            } else {
                cardLinkedRecipe.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = posts.size
}
