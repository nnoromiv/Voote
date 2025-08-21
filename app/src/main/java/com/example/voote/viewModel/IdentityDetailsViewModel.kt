package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import com.example.voote.model.data.DriverLicenceExtractedData
import com.example.voote.model.data.PassportExtractedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IdentityDetailsViewModel : ViewModel() {
    private val _passportExtractedData = MutableStateFlow(PassportExtractedData())
    val passportExtractedData: StateFlow<PassportExtractedData> = _passportExtractedData

    fun setPassportExtractedData(data: PassportExtractedData) {
        if(data.passportNumber.isNotEmpty()) {
            _passportExtractedData.value = data
        }
    }

    private val _driverLicenceExtractedData = MutableStateFlow(DriverLicenceExtractedData())
    val driverLicenceExtractedData: StateFlow<DriverLicenceExtractedData> = _driverLicenceExtractedData

    fun setDriverLicenceExtractedData(data: DriverLicenceExtractedData) {
        if(data.licenceNumber.isNotEmpty()) {
            _driverLicenceExtractedData.value = data
        }
    }

//    private val _faceLivelinessResult = MutableStateFlow(FaceLivelinessResult())
//    val faceLivelinessResult: StateFlow<FaceLivelinessResult> = _faceLivelinessResult


    private val _isExtracted = MutableStateFlow(false)
    val isExtracted: StateFlow<Boolean> = _isExtracted

    fun setIsExtracted(value: Boolean) {
        _isExtracted.value = value
    }

//    fun setFaceLivelinessResult(result: FaceLivelinessResult) {
//        setIsExtracted(false)
//        _faceLivelinessResult.value = result
//    }

    fun clearPassportData() {
        setIsExtracted(false)
        _passportExtractedData.value = PassportExtractedData()
    }

    fun clearDriverLicenceData() {
        setIsExtracted(false)
        _driverLicenceExtractedData.value = DriverLicenceExtractedData()
    }


}