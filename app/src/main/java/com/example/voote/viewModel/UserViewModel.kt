package com.example.voote.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voote.firebase.user.User
import com.example.voote.model.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: MutableStateFlow<UserData?> = _userData

    fun fetchUserData() {
        viewModelScope.launch {
            val user = User().getUser()
            _userData.value = user
        }
    }

    fun cleanUserData() {
        _userData.value = null
    }

}