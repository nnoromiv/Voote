package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenVerificationViewModel: ViewModel() {
    private val _token = MutableStateFlow(List(6) {""})
    val token : StateFlow<List<String>> = _token.asStateFlow()

    fun setToken(index: Int, value: String) {
        _token.value = _token.value.mapIndexed { i, t ->
            if (i == index) value else t
        }
    }

    private  val _storedVerificationId = MutableStateFlow("")
    val storedVerificationId : StateFlow<String> = _storedVerificationId.asStateFlow()

    fun setStoredVerificationId(value: String) {
        _storedVerificationId.value = value
    }

    private  val _resendToken = MutableStateFlow<PhoneAuthProvider.ForceResendingToken?>(null)
    val resendToken : StateFlow<PhoneAuthProvider.ForceResendingToken?> = _resendToken.asStateFlow()

}
