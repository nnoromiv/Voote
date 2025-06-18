package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SignUpViewModel : ViewModel() {

    private val _firstName = MutableStateFlow("")
    val firstName: MutableStateFlow<String> = _firstName

    fun setFirstName(newFirstName: String) {
        _firstName.value = newFirstName
    }

    private val _lastName = MutableStateFlow("")
    val lastName: MutableStateFlow<String> = _lastName

    fun setLastName(newLastName: String) {
        _lastName.value = newLastName
    }

    private val _email = MutableStateFlow("")
    val email: MutableStateFlow<String> = _email

    fun setEmail(newEmail: String) {
        _email.value = newEmail
    }

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: MutableStateFlow<String> = _phoneNumber

    fun setPhoneNumber(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
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

    private val _isPhoneNumberError = MutableStateFlow(false)
    val isPhoneNumberError: MutableStateFlow<Boolean> = _isPhoneNumberError

    fun setIsPhoneNumberError(newIsPhoneNumberError: Boolean) {
        _isPhoneNumberError.value = newIsPhoneNumberError
    }

    private val _phoneNumberErrorMessage = MutableStateFlow("")
    val phoneNumberErrorMessage: MutableStateFlow<String> = _phoneNumberErrorMessage

    fun setPhoneNumberErrorMessage(newPhoneNumberErrorMessage: String) {
        _phoneNumberErrorMessage.value = newPhoneNumberErrorMessage
    }
}