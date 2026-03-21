package com.project.fridgemate.ui.feed

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.fridgemate.databinding.FragmentAddPostBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private val feedViewModel: FeedViewModel by activityViewModels()

    private var selectedImageBytes: ByteArray? = null
    private var selectedMimeType: String = "image/jpeg"

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.ivPostImage.setImageURI(it)
                extractImageBytes(it)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                binding.ivPostImage.setImageBitmap(it)
                val baos = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                selectedImageBytes = baos.toByteArray()
                selectedMimeType = "image/jpeg"
            }
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
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddImage.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnPost.setOnClickListener {
            submitPost()
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

    private fun submitPost() {
        val title = binding.etRecipeTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.etRecipeTitle.error = "Please add a title"
            return
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "Please add a description"
            return
        }

        binding.btnPost.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            var imageUrl: String? = null
            val bytes = selectedImageBytes
            if (bytes != null) {
                imageUrl = feedViewModel.uploadImage(bytes, selectedMimeType)
            }

            feedViewModel.addPost(title, description, imageUrl)
            Toast.makeText(context, "Post added!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun extractImageBytes(uri: Uri) {
        try {
            val contentResolver = requireContext().contentResolver
            selectedMimeType = contentResolver.getType(uri) ?: "image/jpeg"
            contentResolver.openInputStream(uri)?.use { stream ->
                selectedImageBytes = stream.readBytes()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to read image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
