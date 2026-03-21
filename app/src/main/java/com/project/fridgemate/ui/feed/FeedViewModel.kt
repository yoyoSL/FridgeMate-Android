package com.project.fridgemate.ui.feed

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.fridgemate.data.remote.dto.CommentDto
import com.project.fridgemate.data.remote.dto.CreatePostRequest
import com.project.fridgemate.data.remote.dto.PostDto
import com.project.fridgemate.data.remote.dto.PostLocationRequest
import com.project.fridgemate.data.remote.dto.UpdatePostRequest
import com.project.fridgemate.data.repository.FridgeResult
import com.project.fridgemate.data.repository.PostRepository
import kotlinx.coroutines.launch

data class LinkedRecipe(
    val id: String,
    val title: String,
    val cookingTime: String,
    val difficulty: String,
    val imageUrl: String
)

data class Post(
    val id: String,
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
    val isOwner: Boolean = false,
    val linkedRecipe: LinkedRecipe? = null
)

data class Comment(
    val id: String,
    val postId: String,
    val userName: String,
    val text: String,
    val isOwner: Boolean = false
)

class FeedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PostRepository(application.applicationContext)

    private val _posts = MutableLiveData<List<Post>>(emptyList())
    val posts: LiveData<List<Post>> = _posts

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = repository.getPosts()) {
                is FridgeResult.Success -> {
                    _posts.value = result.data.items.map { it.toPost() }
                }
                is FridgeResult.Error -> {
                    _error.value = result.message
                    Log.e("FeedViewModel", "loadPosts error: ${result.message}")
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun toggleLike(post: Post) {
        val current = _posts.value?.map {
            if (it.id == post.id) it.copy(
                isLiked = !it.isLiked,
                likesCount = if (it.isLiked) it.likesCount - 1 else it.likesCount + 1
            ) else it
        } ?: return
        _posts.value = current

        viewModelScope.launch {
            when (val result = repository.toggleLike(post.id)) {
                is FridgeResult.Success -> {
                    _posts.value = _posts.value?.map {
                        if (it.id == post.id) it.copy(
                            isLiked = result.data.liked,
                            likesCount = result.data.likesCount
                        ) else it
                    }
                }
                is FridgeResult.Error -> {
                    _posts.value = _posts.value?.map {
                        if (it.id == post.id) it.copy(
                            isLiked = post.isLiked,
                            likesCount = post.likesCount
                        ) else it
                    }
                    Log.e("FeedViewModel", "toggleLike error: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun addPost(
        title: String,
        description: String,
        imageUrl: String? = null,
        recipeId: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        viewModelScope.launch {
            val location = if (latitude != null && longitude != null)
                PostLocationRequest(lat = latitude, lng = longitude)
            else null

            val request = CreatePostRequest(
                title = title,
                text = description,
                mediaUrls = if (imageUrl != null) listOf(imageUrl) else emptyList(),
                recipeId = recipeId,
                location = location
            )
            when (val result = repository.createPost(request)) {
                is FridgeResult.Success -> {
                    loadPosts()
                }
                is FridgeResult.Error -> {
                    _error.value = result.message
                    Log.e("FeedViewModel", "addPost error: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun editPost(postId: String, newTitle: String, newDescription: String, imageUrl: String? = null) {
        viewModelScope.launch {
            val currentPost = _posts.value?.find { it.id == postId }
            val mediaUrls = when {
                imageUrl != null -> listOf(imageUrl)
                currentPost?.imageUrl?.isNotEmpty() == true -> listOf(currentPost.imageUrl)
                else -> null
            }
            val request = UpdatePostRequest(
                title = newTitle,
                text = newDescription,
                mediaUrls = mediaUrls
            )
            when (val result = repository.updatePost(postId, request)) {
                is FridgeResult.Success -> {
                    loadPosts()
                }
                is FridgeResult.Error -> {
                    _error.value = result.message
                    Log.e("FeedViewModel", "editPost error: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun deletePost(postId: String) {
        _posts.value = _posts.value?.filter { it.id != postId }

        viewModelScope.launch {
            when (val result = repository.deletePost(postId)) {
                is FridgeResult.Error -> {
                    _error.value = result.message
                    loadPosts()
                }
                else -> {}
            }
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            when (val result = repository.getComments(postId)) {
                is FridgeResult.Success -> {
                    _posts.value = _posts.value?.map { post ->
                        if (post.id == postId) {
                            post.copy(comments = result.data.map { it.toComment() })
                        } else post
                    }
                }
                is FridgeResult.Error -> {
                    Log.e("FeedViewModel", "loadComments error: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun addComment(postId: String, text: String) {
        viewModelScope.launch {
            when (val result = repository.createComment(postId, text)) {
                is FridgeResult.Success -> {
                    _posts.value = _posts.value?.map { post ->
                        if (post.id == postId) {
                            val newComments = post.comments.toMutableList()
                            newComments.add(result.data.toComment())
                            post.copy(
                                comments = newComments,
                                commentsCount = post.commentsCount + 1
                            )
                        } else post
                    }
                }
                is FridgeResult.Error -> {
                    _error.value = result.message
                    Log.e("FeedViewModel", "addComment error: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun editComment(postId: String, commentId: String, newText: String) {
        viewModelScope.launch {
            when (val result = repository.updateComment(postId, commentId, newText)) {
                is FridgeResult.Success -> {
                    _posts.value = _posts.value?.map { post ->
                        if (post.id == postId) {
                            post.copy(
                                comments = post.comments.map { comment ->
                                    if (comment.id == commentId) result.data.toComment()
                                    else comment
                                }
                            )
                        } else post
                    }
                }
                is FridgeResult.Error -> {
                    _error.value = result.message
                    Log.e("FeedViewModel", "editComment error: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        _posts.value = _posts.value?.map { post ->
            if (post.id == postId) {
                post.copy(
                    comments = post.comments.filter { it.id != commentId },
                    commentsCount = post.commentsCount - 1
                )
            } else post
        }

        viewModelScope.launch {
            when (val result = repository.deleteComment(postId, commentId)) {
                is FridgeResult.Error -> {
                    _error.value = result.message
                    loadComments(postId)
                }
                else -> {}
            }
        }
    }

    suspend fun uploadImage(imageBytes: ByteArray, mimeType: String): String? {
        return when (val result = repository.uploadImage(imageBytes, mimeType)) {
            is FridgeResult.Success -> result.data
            is FridgeResult.Error -> {
                _error.value = result.message
                Log.e("FeedViewModel", "uploadImage error: ${result.message}")
                null
            }
            else -> null
        }
    }

    private fun PostDto.toPost(): Post {
        val loc = location
        val lng = loc?.coordinates?.getOrNull(0) ?: 0.0
        val lat = loc?.coordinates?.getOrNull(1) ?: 0.0
        val placeName = loc?.placeName
        val city = authorUserId.address?.city

        val recipe = recipeId?.let {
            LinkedRecipe(
                id = it.id,
                title = it.title ?: "",
                cookingTime = it.cookingTime ?: "",
                difficulty = it.difficulty ?: "",
                imageUrl = it.imageUrl ?: ""
            )
        }

        return Post(
            id = id,
            userName = authorUserId.displayName,
            userLocation = placeName ?: city ?: "",
            postTitle = title ?: "",
            description = text,
            likesCount = likesCount,
            commentsCount = commentsCount,
            imageUrl = mediaUrls.firstOrNull() ?: "",
            isLiked = isLiked,
            isOwner = isOwner,
            latitude = lat,
            longitude = lng,
            linkedRecipe = recipe
        )
    }

    private fun CommentDto.toComment(): Comment {
        return Comment(
            id = id,
            postId = postId,
            userName = authorUserId.displayName,
            text = text,
            isOwner = isOwner
        )
    }
}
