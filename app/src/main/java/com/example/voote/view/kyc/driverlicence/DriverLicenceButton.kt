package com.example.voote.view.kyc.driverlicence

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.navigation.AddressVerification
import com.example.voote.navigation.ScanID
import com.example.voote.navigation.homeObject
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.viewModel.KycViewModel
import kotlinx.coroutines.delay

@Composable
fun DriverLicenceButtons(navController: NavController, kycViewModel: KycViewModel) {

    val isLoading = remember { mutableStateOf(false) }
    val activity = LocalActivity.current as Activity

    val kycData by kycViewModel.kycData.collectAsState()
    val driverLicenceNumber by kycViewModel.driverLicenceNumber.collectAsState()
    val driverLicenceExpiryDate by kycViewModel.driverLicenceExpiryDate.collectAsState()
    val isIdError by kycViewModel.isIdError.collectAsState()
    val idErrorMessage by kycViewModel.idErrorMessage.collectAsState()

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading.value = false
    }

    val errorExist = idErrorMessage.isNotEmpty() || isIdError || driverLicenceNumber.isEmpty() || driverLicenceExpiryDate.isEmpty()

    fun handleClick() {
        isLoading.value = true

        try {
            Verification().saveDocumentNumbersToDB(
                null,
                null,
                driverLicenceNumber,
                driverLicenceExpiryDate,

                onSuccess = {
                    isLoading.value = false

                    val documentType = "driverLicence"
                    navController.navigate(ScanID(
                        documentType
                    ))
                }
            )

        } catch (e: Exception) {
            isLoading.value = false
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    val driverLicenceKycDataExist = kycData?.driverLicenceNumber?.isNotEmpty() == true && kycData?.driverLicenceExpiryDate?.isNotEmpty() == true

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(isLoading.value) {
            Loader()
        } else {
            if(driverLicenceKycDataExist) {
                PrimaryButton(
                    text =  "Continue",
                    onClick = {
                        navController.navigate(AddressVerification)
                    },
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            PrimaryButton(
                text = "Scan Licence",
                onClick = { handleClick() },
                enabled = !errorExist,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            if(!driverLicenceKycDataExist) {
                COutlinedButton(
                    text = "Do this later",
                    onClick = {navController.navigate(homeObject)},
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}