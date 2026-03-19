package com.project.fridgemate.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMapView.setOnClickListener {
            requireParentFragment().findNavController()
                .navigate(R.id.action_dashboardFragment_to_mapViewFragment)
        }

        binding.fabAddPost.setOnClickListener {
            requireParentFragment().findNavController()
                .navigate(R.id.action_dashboardFragment_to_addPostFragment)
        }

        setupPosts()
    }

    private fun setupPosts() {
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            binding.rvPosts.adapter = PostAdapter(
                posts = posts,
                onLikeClick = { post ->
                    viewModel.toggleLike(post)
                },
                onAddComment = { postId, text ->
                    viewModel.addComment(postId, "Me", text)
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}