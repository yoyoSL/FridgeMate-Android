package com.project.fridgemate.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Post(
    val id: Int,
    val userName: String,
    val userLocation: String,
    val postTitle: String,
    val description: String,
    val likesCount: Int,
    val commentsCount: Int,
    val imageUrl: String = "",
    val isLiked: Boolean = false,
    val isFavorite: Boolean = false
)

class FeedViewModel : ViewModel() {

    // TODO: ADD REAL DATA FOR POSTS
    private val _posts = MutableLiveData<List<Post>>(
        listOf(
            Post(
                id = 1,
                userName = "Sarah Johnson",
                userLocation = "New York, NY",
                postTitle = "Creamy Tuscan Chicken",
                description = "Just made this amazing dish with the ingredients I had! The sun-dried tomatoes really make it special.",
                likesCount = 42,
                commentsCount = 8
            ),
            Post(
                id = 2,
                userName = "Michael Chen",
                userLocation = "San Francisco, CA",
                postTitle = "Avocado Pasta",
                description = "Quick and healthy dinner ready in 20 minutes!",
                likesCount = 28,
                commentsCount = 5
            ),
            Post(
                id = 3,
                userName = "Emma Davis",
                userLocation = "Chicago, IL",
                postTitle = "Mushroom Risotto",
                description = "Comfort food at its finest. Perfect for a rainy day.",
                likesCount = 65,
                commentsCount = 12
            )
        )
    )
    val posts: LiveData<List<Post>> = _posts

    fun toggleLike(post: Post) {
        val updated = _posts.value?.map {
            if (it.id == post.id) it.copy(
                isLiked = !it.isLiked,
                likesCount = if (it.isLiked) it.likesCount - 1 else it.likesCount + 1
            ) else it
        } ?: return
        _posts.value = updated
        // TODO: API call
    }

}