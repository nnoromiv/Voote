package com.example.voote.view.kyc.passport

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.firebase.data.Status
import com.example.voote.navigation.RouteDriverLicenceVerification
import com.example.voote.navigation.RouteHome
import com.example.voote.navigation.RouteScanID
import com.example.voote.navigation.RouteStatus
import com.example.voote.navigation.toJson
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.KycViewModel
import kotlinx.coroutines.launch

@Composable
fun PassportButton(authManager: AuthViewModel, kycViewModel: KycViewModel, navController: NavController) {

    val isLoading = remember { mutableStateOf(true) }
    val verification = Verification(authManager)
    val coroutineScope = rememberCoroutineScope()

    val kycData by kycViewModel.kycData.collectAsState()
    val passportNumber by kycViewModel.passportNumber.collectAsState()
    val passportExpiryDate by kycViewModel.passportExpiryDate.collectAsState()
    val isIdError by kycViewModel.isIdError.collectAsState()
    val idErrorMessage by kycViewModel.idErrorMessage.collectAsState()

    val errorExist = idErrorMessage.isNotEmpty() || isIdError || passportNumber.isEmpty() || passportExpiryDate.isEmpty()

    fun handleClick() {
        isLoading.value = true

        coroutineScope.launch {
            val result = verification.saveDocumentNumbers(passportNumber, passportExpiryDate, null, null)

            if(result.status == Status.ERROR) {
                isLoading.value = false

                navController.navigate(RouteStatus(
                    status = result.status,
                    nextScreen = ""
                ))

                return@launch
            }

            navController.navigate(RouteStatus(
                status = result.status,
                nextScreen = RouteScanID(documentType = "passport").toJson()
            ))
        }
    }

    val passportKycDataExist = kycData?.passportNumber?.isNotEmpty() == true && kycData?.passportExpiryDate?.isNotEmpty() == true
    val kycDataImage = kycData?.passportImage?.isNotEmpty() == true

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(passportKycDataExist) {
            PrimaryButton(
                text =  "Continue",
                onClick = {
                    if(kycDataImage) {
                        navController.navigate(RouteDriverLicenceVerification)
                    } else {
                        navController.navigate(RouteScanID( documentType = "passport"))
                    }
                },
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        PrimaryButton(
            text =  "Scan Passport",
            onClick = { handleClick() },
            enabled = !errorExist,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        if(!passportKycDataExist) {
            COutlinedButton(
                text = "Do this later",
                onClick = {navController.navigate(RouteHome)},
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }
    }
}