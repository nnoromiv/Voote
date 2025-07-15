package com.example.voote.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.voote.model.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    fun setUserData(userData: UserData?) {
        _userData.value = userData
    }

    fun cleanUserData() {
        _userData.value = null
    }

}