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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.fridgemate.R
import android.widget.TextView
import androidx.fragment.app.viewModels
class SettingsFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }
        view.findViewById<View>(R.id.btnUploadFridgePhoto).setOnClickListener {
            showImageSourceDialog()
        }
        view.findViewById<View>(R.id.btnCopyCode).setOnClickListener {
            copyInviteCode(viewModel.inviteCode.value ?: "")
        }

        view.findViewById<View>(R.id.btnLeaveFridge).setOnClickListener {
            showLeaveFridgeDialog()
        }
        setupMembers(view)
        viewModel.fridgeName.observe(viewLifecycleOwner) { name ->
            view.findViewById<TextView>(R.id.tvFridgeName).text = name
        }
        viewModel.inviteCode.observe(viewLifecycleOwner) { code ->
            view.findViewById<TextView>(R.id.tvInviteCode).text = code
        }
        viewModel.members.observe(viewLifecycleOwner) { members ->
            view.findViewById<TextView>(R.id.tvMembersCount).text = "${members.size} members"
        }
    }
    private fun setupMembers(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMembers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.members.observe(viewLifecycleOwner) { members ->
            recyclerView.adapter = MemberAdapter(members)
        }
    }
    private fun copyInviteCode(code: String) {
        val clipboard = requireContext()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Invite Code", code))
        Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
    }

    private fun showLeaveFridgeDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Leave Fridge?")
            .setMessage("Are you sure you want to leave Family Kitchen?")
            .setPositiveButton("Leave") { _, _ ->
                // TODO: send request to server
                Toast.makeText(context, "Left fridge", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("📷 Camera", "🖼️ Gallery")
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
        // TODO: send to server ?
        Toast.makeText(context, "Got image from camera!", Toast.LENGTH_SHORT).show()
    }
    private fun handleUri(uri: Uri) {
        // TODO:send to server ?
        Toast.makeText(context, "Got image from gallery!", Toast.LENGTH_SHORT).show()
    }
}