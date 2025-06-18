package com.example.voote.view.kyc.passport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.DateInputField
import com.example.voote.ui.components.ErrorHandlingInputField
import com.example.voote.utils.helpers.isValidPassportNumber
import com.example.voote.viewModel.KycViewModel

@Composable
fun PassportForm(kycViewModel: KycViewModel) {

    val passportNumber by kycViewModel.passportNumber.collectAsState()
    val passportExpiryDate by kycViewModel.passportExpiryDate.collectAsState()
    val isIdError by kycViewModel.isIdError.collectAsState()
    val idErrorMessage by kycViewModel.idErrorMessage.collectAsState()

    fun onPassportNumberChange(value: String) {
        val isValid = isValidPassportNumber(value.trim())
        kycViewModel.setIsIdError(!isValid)
        kycViewModel.setIdErrorMessage(if (isValid) "" else "Invalid Passport Number")
        kycViewModel.setPassportNumber(value.trim())
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {

        ErrorHandlingInputField(
            passportNumber,
            onValueChange = { onPassportNumberChange(it.trim()) },
            isError = isIdError,
            errorMessage = idErrorMessage,
            label = "Passport Number",
            imageVector = Icons.Outlined.PermIdentity
        )

        DateInputField(
            label = "Passport Expiry Date",
            date = passportExpiryDate,
            onDateSelected = {
                kycViewModel.setPassportExpiryDate(it)
            },
        )

    }
}
