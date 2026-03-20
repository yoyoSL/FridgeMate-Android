package com.project.fridgemate.ui.feed

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentMapViewBinding
import com.squareup.picasso.Picasso
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapViewFragment : Fragment() {

    private var _binding: FragmentMapViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Configuration.getInstance().userAgentValue = requireContext().packageName
        _binding = FragmentMapViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupListeners()
        observePosts()
    }

    private fun setupMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.setBuiltInZoomControls(false) // Disable default controls
        
        val mapController = binding.mapView.controller
        mapController.setZoom(4.0)
        val startPoint = GeoPoint(39.8283, -98.5795) // Center of US
        mapController.setCenter(startPoint)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCloseDetail.setOnClickListener {
            binding.cvPostDetail.visibility = View.GONE
        }

        binding.btnZoomIn.setOnClickListener {
            binding.mapView.controller.zoomIn()
        }

        binding.btnZoomOut.setOnClickListener {
            binding.mapView.controller.zoomOut()
        }
    }

    private fun observePosts() {
        val circleDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bg_marker_circle)
        val pinDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_pin)
        
        val markerDrawable = if (circleDrawable != null && pinDrawable != null) {
            val tintedPin = DrawableCompat.wrap(pinDrawable).mutate()
            DrawableCompat.setTint(tintedPin, ContextCompat.getColor(requireContext(), R.color.teal_primary))
            
            val layers = arrayOf(circleDrawable, tintedPin)
            val layerDrawable = LayerDrawable(layers)
            
            // Center the pin inside the circle and lift it up slightly
            val padding = 24
            layerDrawable.setLayerInset(1, padding, padding, padding, padding)
            layerDrawable
        } else {
            pinDrawable
        }

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            binding.mapView.overlays.clear()
            posts.forEach { post ->
                if (post.latitude != 0.0 && post.longitude != 0.0) {
                    val marker = Marker(binding.mapView)
                    marker.position = GeoPoint(post.latitude, post.longitude)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    marker.icon = markerDrawable
                    marker.title = post.postTitle
                    
                    marker.setOnMarkerClickListener { _, _ ->
                        showPostDetail(post)
                        true
                    }
                    binding.mapView.overlays.add(marker)
                }
            }
            binding.mapView.invalidate()
        }
    }

    private fun showPostDetail(post: Post) {
        binding.cvPostDetail.visibility = View.VISIBLE
        binding.tvUserName.text = post.userName
        binding.tvLocation.text = post.userLocation
        binding.tvPostTitle.text = post.postTitle
        binding.tvDescription.text = post.description
        binding.tvLikes.text = "${post.likesCount} likes"
        binding.tvComments.text = "${post.commentsCount} comments"

        if (post.imageUrl.isNotEmpty()) {
            binding.ivPostImage.visibility = View.VISIBLE
            Picasso.get().load(post.imageUrl).into(binding.ivPostImage)
        } else {
            binding.ivPostImage.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}