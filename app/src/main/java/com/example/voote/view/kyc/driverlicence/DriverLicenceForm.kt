package com.example.voote.view.kyc.driverlicence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DriveEta
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.DateInputField
import com.example.voote.ui.components.ErrorHandlingInputField
import com.example.voote.utils.helpers.isDriverLicenceValid
import com.example.voote.viewModel.KycViewModel

@Composable
fun DriverLicenceForm(kycViewModel: KycViewModel) {

    val driverLicenceNumber by kycViewModel.driverLicenceNumber.collectAsState()
    val driverLicenceExpiryDate by kycViewModel.driverLicenceExpiryDate.collectAsState()
    val isIdError by kycViewModel.isIdError.collectAsState()
    val idErrorMessage by kycViewModel.idErrorMessage.collectAsState()

    fun onDriverLicenceNumberChange(value: String) {
        val isValid = isDriverLicenceValid(value.trim())
        kycViewModel.setIsIdError(!isValid)
        kycViewModel.setIdErrorMessage(if (isValid) "" else "Invalid Driver Licence Number")
        kycViewModel.setDriverLicenceNumber(value.trim())
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {

        ErrorHandlingInputField(
            driverLicenceNumber,
            onValueChange = { onDriverLicenceNumberChange(it.trim()) },
            isError = isIdError,
            errorMessage = idErrorMessage,
            label = "Driver Licence Number",
            imageVector = Icons.Outlined.DriveEta
        )

        DateInputField(
            label = "Driver Licence Expiry Date",
            date = driverLicenceExpiryDate,
            onDateSelected = {
                kycViewModel.setDriverLicenceExpiryDate(it)
            },
        )
    }
}