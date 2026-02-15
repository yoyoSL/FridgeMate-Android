package com.project.fridgemate.ui.profile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DietaryPrefViewModel : ViewModel() {

    private val _selectedPreference = MutableLiveData<String>("NONE")
    val selectedPreference: LiveData<String> = _selectedPreference

    fun onPreferenceSelected(preference: String) {
        _selectedPreference.value = preference
    }
}