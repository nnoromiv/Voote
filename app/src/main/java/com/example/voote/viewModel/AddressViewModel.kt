package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddressViewModel: ViewModel() {
    private val _country = MutableStateFlow("")
    val country: StateFlow<String> = _country

    fun setCountry(value: String) {
        _country.value = value
    }

    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state

    fun setState(value: String) {
        _state.value = value
    }

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city

    fun setCity(value: String) {
        _city.value = value
    }

    private val _street = MutableStateFlow("")
    val street: StateFlow<String> = _street

    fun setStreet(value: String) {
        _street.value = value
    }

    private val _postCode = MutableStateFlow("")
    val postCode: StateFlow<String> = _postCode

    fun setPostCode(value: String) {
        _postCode.value = value
    }

}