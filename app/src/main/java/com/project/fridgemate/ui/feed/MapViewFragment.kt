package com.project.fridgemate.ui.feed

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
import com.project.fridgemate.BuildConfig
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentMapViewBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapViewFragment : Fragment() {

    private var _binding: FragmentMapViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()

    private var tabLayoutMediator: TabLayoutMediator? = null

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
        binding.mapView.setBuiltInZoomControls(false)
        
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

        // Dynamic height adjustment for ViewPager2
        binding.vpPostDetail.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateViewPagerHeight(position)
            }
        })
    }

    private fun updateViewPagerHeight(position: Int) {
        binding.vpPostDetail.post {
            val recyclerView = binding.vpPostDetail.getChildAt(0) as? RecyclerView
            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position)
            val itemView = viewHolder?.itemView

            itemView?.post {
                val container = itemView.findViewById<View>(R.id.llItemContainer)
                val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(itemView.width, View.MeasureSpec.EXACTLY)
                val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                container.measure(wMeasureSpec, hMeasureSpec)

                val targetHeight = container.measuredHeight
                if (binding.vpPostDetail.layoutParams.height != targetHeight) {
                    val params = binding.vpPostDetail.layoutParams
                    params.height = targetHeight
                    binding.vpPostDetail.layoutParams = params
                }
            }
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
            val padding = 24
            layerDrawable.setLayerInset(1, padding, padding, padding, padding)
            layerDrawable
        } else {
            pinDrawable
        }

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            binding.mapView.overlays.clear()
            
            val validPosts = posts.filter { it.latitude != 0.0 || it.longitude != 0.0 }
            
            if (validPosts.isEmpty()) {
                binding.cvNoPosts.visibility = View.VISIBLE
            } else {
                binding.cvNoPosts.visibility = View.GONE
                
                // Group posts by location to handle overlapping pins
                val groupedPosts = validPosts.groupBy { GeoPoint(it.latitude, it.longitude) }
                
                groupedPosts.forEach { (point, postsAtLocation) ->
                    val marker = Marker(binding.mapView)
                    marker.position = point
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    marker.icon = markerDrawable
                    marker.title = if (postsAtLocation.size > 1) 
                        "${postsAtLocation.size} posts here" 
                    else postsAtLocation[0].postTitle
                    
                    marker.setOnMarkerClickListener { _, _ ->
                        showPostDetails(postsAtLocation)
                        true
                    }
                    binding.mapView.overlays.add(marker)
                }
            }
            binding.mapView.invalidate()
        }
    }

    private fun showPostDetails(posts: List<Post>) {
        binding.cvPostDetail.visibility = View.VISIBLE
        
        tabLayoutMediator?.detach()
        
        val adapter = MapPostDetailAdapter(posts)
        binding.vpPostDetail.adapter = adapter
        
        // Reset height for the first item
        binding.vpPostDetail.post {
            updateViewPagerHeight(0)
        }
        
        if (posts.size > 1) {
            binding.tlDots.visibility = View.VISIBLE
            tabLayoutMediator = TabLayoutMediator(binding.tlDots, binding.vpPostDetail) { _, _ -> }
            tabLayoutMediator?.attach()
        } else {
            binding.tlDots.visibility = View.GONE
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
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        _binding = null
    }
}