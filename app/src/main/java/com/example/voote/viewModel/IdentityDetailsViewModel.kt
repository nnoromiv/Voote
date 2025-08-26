// IdentityDetailsViewModel.kt
package com.example.voote.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voote.firebase.auth.Verification
import com.example.voote.firebase.data.STATUS
import com.example.voote.model.data.DriverLicenceExtractedData
import com.example.voote.model.data.PassportExtractedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class IdentityDetailsViewModel : ViewModel() {

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _uploadMessage = MutableStateFlow<String?>(null)
    val uploadMessage: StateFlow<String?> = _uploadMessage

    private val _isExtracted = MutableStateFlow(false)
    val isExtracted: StateFlow<Boolean> = _isExtracted

    private val _passportData = MutableStateFlow(PassportExtractedData())
    val passportData: StateFlow<PassportExtractedData> = _passportData

    private val _driverLicenceData = MutableStateFlow(DriverLicenceExtractedData())
    val driverLicenceData: StateFlow<DriverLicenceExtractedData> = _driverLicenceData

    fun setPassportExtractedData(data: PassportExtractedData) {
        _passportData.value = data
    }

    fun setDriverLicenceExtractedData(data: DriverLicenceExtractedData) {
        _driverLicenceData.value = data
    }

    fun setIsExtracted(value: Boolean) {
        _isExtracted.value = value
    }

    fun uploadPassportData(uploadData: PassportExtractedData, verification: Verification, context: Context) {
        if (_isUploading.value) return // prevent duplicate uploads
        _isUploading.value = true

        viewModelScope.launch {
            try {
                val uploadResult = verification.uploadPassportDataToFirebase(uploadData)

                if (uploadResult.status == STATUS.ERROR) {
                    _uploadMessage.value = "Error: ${uploadResult.message}"
                    Toast.makeText(context, _uploadMessage.value, Toast.LENGTH_SHORT).show()
                    Log.e("UploadPassport", "Error uploading: ${uploadResult.message}")
                } else {
                    _uploadMessage.value = "Passport uploaded successfully"
                    Toast.makeText(context, _uploadMessage.value, Toast.LENGTH_SHORT).show()
                    Log.d("UploadPassport", "Scanned: $uploadData")
                }
            } catch (e: Exception) {
                _uploadMessage.value = "Unexpected error: ${e.message}"
                Toast.makeText(context, _uploadMessage.value, Toast.LENGTH_SHORT).show()
                Log.e("UploadPassport", "Upload crash", e)
            } finally {
                _isUploading.value = false
                _isExtracted.value = true
            }
        }
    }

    fun uploadDriverLicenceData(uploadData: DriverLicenceExtractedData, verification: Verification, context: Context) {
        if (_isUploading.value) return
        _isUploading.value = true

        viewModelScope.launch {
            try {
                val uploadResult = verification.uploadDriverLicenceDataToFirebase(uploadData)

                if (uploadResult.status == STATUS.ERROR) {
                    _uploadMessage.value = "Error: ${uploadResult.message}"
                    Toast.makeText(context, _uploadMessage.value, Toast.LENGTH_SHORT).show()
                    Log.e("UploadLicence", "Error uploading: ${uploadResult.message}")
                } else {
                    _uploadMessage.value = "Licence uploaded successfully"
                    Toast.makeText(context, _uploadMessage.value, Toast.LENGTH_SHORT).show()
                    Log.d("UploadLicence", "Scanned: $uploadData")
                }
            } catch (e: Exception) {
                _uploadMessage.value = "Unexpected error: ${e.message}"
                Toast.makeText(context, _uploadMessage.value, Toast.LENGTH_SHORT).show()
                Log.e("UploadLicence", "Upload crash", e)
            } finally {
                _isUploading.value = false
                _isExtracted.value = true
            }
        }
    }
}
