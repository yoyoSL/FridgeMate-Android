package com.project.fridgemate.ui.settings

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.fridgemate.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedFridgeViewModel by viewModels()

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { handleBitmap(it) }
        }

    private val pickFromGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleUri(it) }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) takePictureLauncher.launch(null)
            else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()

        viewModel.loadFridge()
    }

    private fun setupObservers() {
        viewModel.hasFridge.observe(viewLifecycleOwner) { hasFridge ->
            when (hasFridge) {
                true -> {
                    binding.cardSharedFridge.visibility = View.VISIBLE
                    binding.cardNoFridge.visibility = View.GONE
                    binding.cardFridgeScanner.visibility = View.VISIBLE
                }
                false -> {
                    binding.cardSharedFridge.visibility = View.GONE
                    binding.cardNoFridge.visibility = View.VISIBLE
                    binding.cardFridgeScanner.visibility = View.GONE
                }
                null -> {}
            }
        }

        viewModel.fridgeName.observe(viewLifecycleOwner) { name ->
            binding.tvFridgeName.text = name
        }

        viewModel.inviteCode.observe(viewLifecycleOwner) { code ->
            binding.tvInviteCode.text = code
        }

        viewModel.members.observe(viewLifecycleOwner) { members ->
            binding.tvMembersCount.text = "${members.size} members"
            binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
            binding.rvMembers.adapter = MemberAdapter(members)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.actionSuccess.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearActionSuccess()
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnUploadFridgePhoto.setOnClickListener {
            showImageSourceDialog()
        }

        binding.btnCopyCode.setOnClickListener {
            copyInviteCode(viewModel.inviteCode.value ?: "")
        }

        binding.btnLeaveFridge.setOnClickListener {
            showLeaveFridgeDialog()
        }

        binding.btnCreateFridge.setOnClickListener {
            val name = binding.etFridgeName.text.toString()
            viewModel.createFridge(name)
        }

        binding.btnJoinFridge.setOnClickListener {
            val code = binding.etInviteCode.text.toString()
            viewModel.joinFridge(code)
        }
    }

    private fun copyInviteCode(code: String) {
        val clipboard = requireContext()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Invite Code", code))
        Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
    }

    private fun showLeaveFridgeDialog() {
        val fridgeName = viewModel.fridgeName.value ?: "this fridge"
        AlertDialog.Builder(requireContext())
            .setTitle("Leave Fridge?")
            .setMessage("Are you sure you want to leave $fridgeName?")
            .setPositiveButton("Leave") { _, _ -> viewModel.leaveFridge() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose image source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission.launch(Manifest.permission.CAMERA)
                    1 -> pickFromGalleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun handleBitmap(bitmap: Bitmap) {
        Toast.makeText(context, "Got image from camera!", Toast.LENGTH_SHORT).show()
    }

    private fun handleUri(uri: Uri) {
        Toast.makeText(context, "Got image from gallery!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
