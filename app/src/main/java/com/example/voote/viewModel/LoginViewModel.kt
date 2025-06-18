package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LoginViewModel : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: MutableStateFlow<String> = _email

    fun setEmail(newEmail: String) {
        _email.value = newEmail
    }

    private val _password = MutableStateFlow("")
    val password: MutableStateFlow<String> = _password

    fun setPassword(newPassword: String) {
        _password.value = newPassword
    }

    private val _isEmailError = MutableStateFlow(false)
    val isEmailError: MutableStateFlow<Boolean> = _isEmailError

    fun setIsEmailError(newIsEmailError: Boolean) {
        _isEmailError.value = newIsEmailError
    }

    private val _emailErrorMessage = MutableStateFlow("")
    val emailErrorMessage: MutableStateFlow<String> = _emailErrorMessage

    fun setEmailErrorMessage(newEmailErrorMessage: String) {
        _emailErrorMessage.value = newEmailErrorMessage
    }

}