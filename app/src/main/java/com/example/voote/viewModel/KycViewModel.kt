package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voote.firebase.user.User
import com.example.voote.model.data.KycData
import com.example.voote.utils.helpers.convertLongToDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KycViewModel : ViewModel() {
    private val _kycCompletionPercentage = MutableStateFlow(0)
    val kycCompletionPercentage: StateFlow<Int> = _kycCompletionPercentage.asStateFlow()

    fun setKycCompletionPercentage(value: Int) {
        _kycCompletionPercentage.value = value
    }

    private val _passportNumber = MutableStateFlow("")
    val passportNumber: StateFlow<String> = _passportNumber.asStateFlow()

    fun setPassportNumber(value: String) {
        _passportNumber.value = value
    }

    private val _passportExpiryDate = MutableStateFlow("")
    val passportExpiryDate: StateFlow<String> = _passportExpiryDate.asStateFlow()

    fun setPassportExpiryDate(value: Long) {
        _passportExpiryDate.value = convertLongToDate(value)
    }

    private val _driverLicenceNumber = MutableStateFlow("")
    val driverLicenceNumber: StateFlow<String> = _driverLicenceNumber.asStateFlow()

    fun setDriverLicenceNumber(value: String) {
        _driverLicenceNumber.value = value
    }

    private val _driverLicenceExpiryDate = MutableStateFlow("")
    val driverLicenceExpiryDate: StateFlow<String> = _driverLicenceExpiryDate.asStateFlow()

    fun setDriverLicenceExpiryDate(value: Long) {
        _driverLicenceExpiryDate.value = convertLongToDate(value)
    }

    private val _isIdError = MutableStateFlow(false)
    val isIdError: StateFlow<Boolean> = _isIdError.asStateFlow()

    fun setIsIdError(value: Boolean) {
        _isIdError.value = value
    }

    private val _idErrorMessage = MutableStateFlow("")
    val idErrorMessage: StateFlow<String> = _idErrorMessage.asStateFlow()

    fun setIdErrorMessage(value: String) {
        _idErrorMessage.value = value
    }

    private val _kycData = MutableStateFlow<KycData?>(null)
    val kycData: MutableStateFlow<KycData?> = _kycData

    fun fetchKycData() {
        viewModelScope.launch {
            val user = User().getKycData()
            _kycData.value = user
            setKycCompletionPercentage(user?.calculateEmptinessPercentage() ?: 0)
        }
    }

    fun cleanKycData() {
        _kycData.value = null
    }

}
