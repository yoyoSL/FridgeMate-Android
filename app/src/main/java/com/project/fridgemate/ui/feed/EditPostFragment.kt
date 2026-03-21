package com.project.fridgemate.ui.feed

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.fridgemate.databinding.FragmentEditPostBinding

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private val feedViewModel: FeedViewModel by activityViewModels()

    // get the postid from nav
    private var postId: Int = -1
    private var currentTitle: String = ""
    private var currentDescription: String = ""

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { binding.ivPostImage.setImageURI(it) }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { binding.ivPostImage.setImageBitmap(it) }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) takePictureLauncher.launch(null)
            else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }

    private val requestGalleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) pickImageLauncher.launch("image/*")
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getInt("postId") ?: -1
        currentTitle = arguments?.getString("postTitle") ?: ""
        currentDescription = arguments?.getString("postDescription") ?: ""

        // with the current details fill the fields
        binding.etRecipeTitle.setText(currentTitle)
        binding.etDescription.setText(currentDescription)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnChangeImage.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("📷 Camera", "🖼️ Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose image source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission.launch(Manifest.permission.CAMERA)
                    1 -> requestGalleryPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            .show()
    }

    private fun saveChanges() {
        val newTitle = binding.etRecipeTitle.text.toString().trim()
        val newDescription = binding.etDescription.text.toString().trim()

        if (newTitle.isEmpty()) {
            binding.etRecipeTitle.error = "Please add a title"
            return
        }
        if (newDescription.isEmpty()) {
            binding.etDescription.error = "Please add a description"
            return
        }

        feedViewModel.editPost(postId, newTitle, newDescription)
        Toast.makeText(context, "Post updated!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}