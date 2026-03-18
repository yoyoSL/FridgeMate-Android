package com.project.fridgemate.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.fridgemate.data.remote.ApiClient
import com.project.fridgemate.data.remote.dto.AddressDto
import com.project.fridgemate.data.remote.dto.UpdateProfileRequest
import com.project.fridgemate.data.remote.dto.UserDto
import com.project.fridgemate.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _user = MutableLiveData<UserDto?>()
    val user: LiveData<UserDto?> = _user

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _saveSuccess = MutableLiveData<Boolean>(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

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

    private val _selectedPreference = MutableLiveData<String>("NONE")
    val selectedPreference: LiveData<String> = _selectedPreference

    fun loadProfile() {
        val userId = ApiClient.getTokenManager().userId ?: return
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = userRepository.getUserById(userId)
                _user.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveProfile(name: String, location: String, allergies: List<String>) {
        val userId = ApiClient.getTokenManager().userId ?: return
        viewModelScope.launch {
            _loading.value = true
            try {
                val request = UpdateProfileRequest(
                    displayName = name,
                    address = AddressDto(fullAddress = location),
                    allergies = allergies
                )
                val result = userRepository.updateProfile(userId, request)
                _user.value = result
                _saveSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun onPreferenceSelected(preference: String) {
        _selectedPreference.value = preference
    }

    fun toggleAllergy(name: String, isChecked: Boolean) {
        val updated = _allergies.value?.map {
            if (it.name == name) it.copy(isChecked = isChecked) else it
        } ?: return
        _allergies.value = updated
    }

    fun clearSaveSuccess() {
        _saveSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
