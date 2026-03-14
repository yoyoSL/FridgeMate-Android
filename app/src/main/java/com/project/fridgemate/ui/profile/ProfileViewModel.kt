package com.project.fridgemate.ui.profile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _fullName = MutableLiveData<String>("Alex Johnson")
    val fullName: LiveData<String> = _fullName

    private val _location = MutableLiveData<String>("San Francisco, CA")
    val location: LiveData<String> = _location

    private val _selectedPreference = MutableLiveData<String>("NONE")
    val selectedPreference: LiveData<String> = _selectedPreference

    private val _allergies = MutableLiveData<List<AllergyItem>>(
        listOf(
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
    )
    val allergies: LiveData<List<AllergyItem>> = _allergies

    fun saveProfile(name: String, location: String) {
        _fullName.value = name
        _location.value = location
        // TODO: save in database
    }

    fun onPreferenceSelected(preference: String) {
        _selectedPreference.value = preference
        // TODO:save in database
    }

    fun toggleAllergy(name: String, isChecked: Boolean) {
        val updated = _allergies.value?.map {
            if (it.name == name) it.copy(isChecked = isChecked) else it
        } ?: return
        _allergies.value = updated
        // TODO: save in database
    }
}