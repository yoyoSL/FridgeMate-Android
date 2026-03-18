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
    val comments: List<Comment> = emptyList()
)
data class Comment(
    val id: Int,
    val userName: String,
    val text: String
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
                description = "Just made this amazing dish!",
                likesCount = 42,
                commentsCount = 8,
                comments = listOf(
                    Comment(1, "Alex Martinez", "This looks absolutely delicious! Can you share the full recipe?"),
                    Comment(2, "Lisa Brown", "Made this last night and my family loved it! 🔥"),
                    Comment(3, "James Wilson", "What herbs did you use? Looks amazing!")
                )
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
    fun addPost(title: String, description: String) {
        val current = _posts.value?.toMutableList() ?: mutableListOf()
        val newPost = Post(
            id = current.size + 1,
            userName = "Me",
            userLocation = "My Location",
            postTitle = title,
            description = description,
            likesCount = 0,
            commentsCount = 0
        )
        current.add(0, newPost)
        _posts.value = current
        // TODO: Send API
    }

}