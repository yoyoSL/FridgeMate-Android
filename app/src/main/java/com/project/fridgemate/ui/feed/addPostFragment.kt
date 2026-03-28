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
import androidx.navigation.fragment.navArgs
import com.project.fridgemate.R
import com.project.fridgemate.databinding.FragmentAddPostBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private val feedViewModel: FeedViewModel by activityViewModels()
    private val args: AddPostFragmentArgs by navArgs()

    private var selectedImageBytes: ByteArray? = null
    private var selectedMimeType: String = "image/jpeg"

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.ivPostImage.setImageURI(it)
                binding.layoutAddImage.visibility = View.GONE
                extractImageBytes(it)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                binding.ivPostImage.setImageBitmap(it)
                binding.layoutAddImage.visibility = View.GONE
                val baos = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 85, baos)
                selectedImageBytes = baos.toByteArray()
                selectedMimeType = "image/jpeg"
            }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) takePictureLauncher.launch(null)
            else Toast.makeText(context, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
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

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddImage.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnPost.setOnClickListener {
            submitPost()
        }

        if (args.prefillTitle.isNotEmpty()) {
            binding.etRecipeTitle.setText(args.prefillTitle)
        }
        if (args.prefillDescription.isNotEmpty()) {
            binding.etDescription.setText(args.prefillDescription)
            binding.etDescription.setSelection(binding.etDescription.text?.length ?: 0)
        }

        if (args.prefillRecipeId.isNotEmpty() && args.prefillRecipeName.isNotEmpty()) {
            binding.cardRecipePreview.visibility = View.VISIBLE
            binding.tvRecipePreviewTitle.text = args.prefillRecipeName
            val info = buildString {
                if (args.prefillRecipeTime.isNotEmpty()) append(args.prefillRecipeTime)
                if (args.prefillRecipeDifficulty.isNotEmpty()) {
                    if (isNotEmpty()) append(" · ")
                    append(args.prefillRecipeDifficulty)
                }
            }
            binding.tvRecipePreviewInfo.text = info
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf(
            getString(R.string.source_camera),
            getString(R.string.source_gallery)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.choose_image_source))
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
            binding.etRecipeTitle.error = getString(R.string.error_enter_title)
            return
        }
        if (description.isEmpty()) {
            binding.etDescription.error = getString(R.string.error_enter_description)
            return
        }

        binding.btnPost.isEnabled = false
        binding.loadingOverlay.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            var imageUrl: String? = null
            val bytes = selectedImageBytes
            if (bytes != null) {
                imageUrl = feedViewModel.uploadImage(bytes, selectedMimeType)
                if (imageUrl == null) {
                    binding.btnPost.isEnabled = true
                    binding.loadingOverlay.visibility = View.GONE
                    return@launch
                }
            }

            val recipeId = args.prefillRecipeId.ifEmpty { null }
            feedViewModel.addPost(title, description, imageUrl, recipeId)
            binding.loadingOverlay.visibility = View.GONE
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
            Toast.makeText(context, getString(R.string.error_read_image), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
