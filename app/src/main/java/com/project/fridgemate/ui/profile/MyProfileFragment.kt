package com.project.fridgemate.ui.profile
import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.project.fridgemate.R
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast

class MyProfileFragment : Fragment() {
    private lateinit var allergyAdapter: AllergyAdapter
    private val dietaryViewModel: DietaryPrefViewModel by activityViewModels()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                view?.findViewById<ShapeableImageView>(R.id.profile_pic)
                    ?.setImageURI(it)
            }
        }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }
        view.findViewById<View>(R.id.btnChangePhoto).setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
        view.findViewById<View>(R.id.btn_save_changes).setOnClickListener {
            saveChanges(view)
        }
        setupAllergies(view)
    }
    private fun setupAllergies(view: View) {
        val allergies = listOf(
            AllergyItem("Peanuts"),
            AllergyItem("Tree Nuts"),
            AllergyItem("Dairy"),
            AllergyItem("Eggs"),
            AllergyItem("Soy"),
            AllergyItem("Wheat/Gluten"),
            AllergyItem("Fish"),
            AllergyItem("Shellfish"),
            AllergyItem("Sesame")
        )
        allergyAdapter = AllergyAdapter(allergies)
        view.findViewById<RecyclerView>(R.id.rvAllergies).apply {
            this.adapter = allergyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        // val selected = adapter.getSelectedAllergies()
        // TODO: PUT /users/me { "allergies": selected }
    }
    private fun saveChanges(view: View) {
        val fullName  = view.findViewById<TextInputEditText>(R.id.etFullName)
            .text.toString().trim()
        val location  = view.findViewById<TextInputEditText>(R.id.etLocation)
            .text.toString().trim()
        val diet = dietaryViewModel.selectedPreference.value ?: "NONE"
        val allergies = allergyAdapter.getSelectedAllergies()

        // TODO: PUT /users/me
        Toast.makeText(context, "Diet: $diet | Allergies: $allergies", Toast.LENGTH_SHORT).show()
    }
}
