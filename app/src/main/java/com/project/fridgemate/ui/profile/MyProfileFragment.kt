package com.project.fridgemate.ui.profile

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.fridgemate.databinding.FragmentMyProfileBinding

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var allergyAdapter: AllergyAdapter
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { binding.profilePic.setImageURI(it) }
        }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) pickImageLauncher.launch("image/*")
        }
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { binding.profilePic.setImageBitmap(it) }
        }
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) takePictureLauncher.launch(null)
            else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAllergies()
        setupClickListeners()
        observeViewModel()

        profileViewModel.loadProfile()
    }

    private fun setupAllergies() {
        allergyAdapter = AllergyAdapter(
            profileViewModel.allergies.value ?: emptyList()
        ) { name, isChecked ->
            profileViewModel.toggleAllergy(name, isChecked)
        }
        binding.rvAllergies.apply {
            adapter = allergyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnChangePhoto.setOnClickListener { showImageSourceDialog() }
        binding.btnSaveChanges.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val allergies = allergyAdapter.getSelectedAllergies()
            profileViewModel.saveProfile(fullName, location, allergies)
        }
    }

    private fun observeViewModel() {
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            binding.etFullName.setText(user.displayName)
            binding.etLocation.setText(user.address?.fullAddress ?: "")
            allergyAdapter.setSelectedAllergies(user.allergies)
        }

        profileViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSaveChanges.isEnabled = !isLoading
        }

        profileViewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
                profileViewModel.clearSaveSuccess()
            }
        }

        profileViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                profileViewModel.clearError()
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose image source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission.launch(Manifest.permission.CAMERA)
                    1 -> requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
