package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletViewModel: ViewModel() {
    private val _address = MutableStateFlow<String>("")
    val address : StateFlow<String> = _address.asStateFlow()

    fun setAddress(value: String) {
        _address.value = value
    }
}