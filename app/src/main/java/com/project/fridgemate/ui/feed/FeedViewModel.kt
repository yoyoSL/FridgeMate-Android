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
    val comments: List<Comment> = emptyList(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isOwner: Boolean = false
)
data class Comment(
    val id: Int,
    val userName: String,
    val text: String,
    val isOwner: Boolean = false
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
                description = "Just made this amazing dish! The sun-dried tomatoes really make it special.",
                likesCount = 42,
                commentsCount = 8,
                imageUrl = "https://www.thecomfortofcooking.com/wp-content/uploads/2019/02/CreamyTuscanChicken-5.jpg",
                comments = listOf(
                    Comment(1, "Alex Martinez", "This looks absolutely delicious! Can you share the full recipe?"),
                    Comment(2, "Lisa Brown", "Made this last night and my family loved it! 🔥"),
                    Comment(3, "James Wilson", "What herbs did you use? Looks amazing!")
                ),
                latitude = 40.7128,
                longitude = -74.0060
            ),
            Post(
                id = 2,
                userName = "Michael Chen",
                userLocation = "San Francisco, CA",
                postTitle = "Avocado Pasta",
                description = "Quick and healthy dinner ready in 20 minutes!",
                likesCount = 28,
                commentsCount = 5,
                latitude = 37.7749,
                longitude = -122.4194
            ),
            Post(
                id = 3,
                userName = "Emma Davis",
                userLocation = "Chicago, IL",
                postTitle = "Mushroom Risotto",
                description = "Comfort food at its finest. Perfect for a rainy day.",
                likesCount = 65,
                commentsCount = 12,
                latitude = 41.8781,
                longitude = -87.6298
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
            commentsCount = 0 ,
            isOwner = true
        )
        current.add(0, newPost)
        _posts.value = current
        // TODO: Send API
    }
    fun addComment(postId: Int, userName: String, text: String) {
        val updated = _posts.value?.map { post ->
            if (post.id == postId) {
                val newComments = post.comments.toMutableList()
                newComments.add(Comment(
                    id = post.comments.size + 1,
                    userName = userName,
                    text = text,
                    isOwner = true
                ))
                post.copy(
                    comments = newComments,
                    commentsCount = post.commentsCount + 1
                )
            } else post
        } ?: return
        _posts.value = updated
        // TODO: API call
    }
    fun deletePost(postId: Int) {
        val updated = _posts.value?.filter { it.id != postId } ?: return
        _posts.value = updated
        // TODO: API call
    }

    fun editPost(postId: Int, newTitle: String, newDescription: String) {
        val updated = _posts.value?.map {
            if (it.id == postId) it.copy(
                postTitle = newTitle,
                description = newDescription
            ) else it
        } ?: return
        _posts.value = updated
        // TODO: API call
    }
    fun deleteComment(postId: Int, commentId: Int) {
        val updated = _posts.value?.map { post ->
            if (post.id == postId) {
                post.copy(
                    comments = post.comments.filter { it.id != commentId },
                    commentsCount = post.commentsCount - 1
                )
            } else post
        } ?: return
        _posts.value = updated
        // TODO: API call
    }

    fun editComment(postId: Int, commentId: Int, newText: String) {
        val updated = _posts.value?.map { post ->
            if (post.id == postId) {
                post.copy(
                    comments = post.comments.map { comment ->
                        if (comment.id == commentId) comment.copy(text = newText)
                        else comment
                    }
                )
            } else post
        } ?: return
        _posts.value = updated
        // TODO: API call
    }

}