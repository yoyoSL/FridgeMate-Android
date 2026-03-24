package com.project.fridgemate.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.fridgemate.databinding.FragmentMyPostsBinding

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()
    private var postAdapter: PostAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupPosts()
    }

    private fun setupPosts() {
        binding.rvMyPosts.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onAddComment = { postId, text -> viewModel.addComment(postId, text) },
            onDeleteClick = { post -> viewModel.deletePost(post.id) },
            onEditClick = { post ->
                val action = MyPostsFragmentDirections
                    .actionMyPostsFragmentToEditPostFragment(
                        postId = post.id,
                        postTitle = post.postTitle,
                        postDescription = post.description,
                        postImageUrl = post.imageUrl,
                        linkedRecipeName = post.linkedRecipe?.title ?: "",
                        linkedRecipeTime = post.linkedRecipe?.cookingTime ?: "",
                        linkedRecipeDifficulty = post.linkedRecipe?.difficulty ?: ""
                    )
                findNavController().navigate(action)
            },
            onDeleteComment = { postId, commentId -> viewModel.deleteComment(postId, commentId) },
            onEditComment = { postId, commentId, newText -> viewModel.editComment(postId, commentId, newText) },
            onExpandComments = { postId -> viewModel.loadComments(postId) },
            onRecipeClick = { recipe ->
                val action = MyPostsFragmentDirections
                    .actionMyPostsFragmentToRecipeDetailFragment(
                        serverRecipeId = recipe.id
                    )
                findNavController().navigate(action)
            }
        )
        binding.rvMyPosts.adapter = postAdapter

        viewModel.posts.observe(viewLifecycleOwner) { allPosts ->
            val myPosts = allPosts.filter { it.isOwner }

            binding.progressBar.visibility = View.GONE

            if (myPosts.isEmpty()) {
                binding.rvMyPosts.visibility = View.GONE
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.rvMyPosts.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                postAdapter?.submitList(myPosts)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
