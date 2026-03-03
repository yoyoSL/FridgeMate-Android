package com.project.fridgemate.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedFridgeViewModel : ViewModel() {

    private val _fridgeName = MutableLiveData<String>("Family Kitchen")
    val fridgeName: LiveData<String> = _fridgeName

    // invite code
    private val _inviteCode = MutableLiveData<String>("FRIDGE-2024-XY7K")
    val inviteCode: LiveData<String> = _inviteCode
    // names of members
    private val _members = MutableLiveData<List<Member>>(
        listOf(
            Member("Alex Johnson", isCurrentUser = true),
            Member("Sarah Johnson"),
            Member("Mike Johnson")
        )
    )
    val members: LiveData<List<Member>> = _members

    // num of members
    val membersCount: LiveData<Int> = MutableLiveData(3)
}