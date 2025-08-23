package com.example.voote.view.kyc.driverlicence

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
import com.example.voote.firebase.data.AUDIT
import com.example.voote.firebase.data.STATUS
import com.example.voote.firebase.user.User
import com.example.voote.navigation.RouteAddressVerification
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
fun DriverLicenceButtons(authManager: AuthViewModel, kycViewModel: KycViewModel, navController: NavController) {

    val isLoading = remember { mutableStateOf(false) }
    val verification = Verification(authManager)
    val coroutineScope = rememberCoroutineScope()
    val kycData by kycViewModel.kycData.collectAsState()
    val driverLicenceNumber by kycViewModel.driverLicenceNumber.collectAsState()
    val driverLicenceExpiryDate by kycViewModel.driverLicenceExpiryDate.collectAsState()
    val isIdError by kycViewModel.isIdError.collectAsState()
    val idErrorMessage by kycViewModel.idErrorMessage.collectAsState()
    val uid = authManager.userUid().toString()

    val errorExist = idErrorMessage.isNotEmpty() || isIdError || driverLicenceNumber.isEmpty() || driverLicenceExpiryDate.isEmpty()

    fun handleClick() {
        isLoading.value = true

        coroutineScope.launch {
            val result = verification.saveDocumentNumbers(null, null, driverLicenceNumber, driverLicenceExpiryDate)

            if(result.status == STATUS.ERROR) {
                isLoading.value = false
                User(uid).writeAuditLog(AUDIT.KYC, STATUS.ERROR, "Licence details saved")

                navController.navigate(RouteStatus(
                    status = result.status,
                    nextScreen = ""
                ))

                return@launch
            }

            User(uid).writeAuditLog(AUDIT.KYC, STATUS.SUCCESS, "Licence details saved")

            navController.navigate(RouteStatus(
                status = result.status,
                nextScreen = RouteScanID( documentType = "driverLicence").toJson()
                ))

        }

    }

    val driverLicenceKycDataExist = kycData?.driverLicenceNumber?.isNotEmpty() == true && kycData?.driverLicenceExpiryDate?.isNotEmpty() == true
    val kycDataImage = kycData?.driverLicenceImage?.isNotEmpty() == true

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(driverLicenceKycDataExist) {
            PrimaryButton(
                text =  "Continue",
                onClick = {
                    if(kycDataImage) {
                        navController.navigate(RouteAddressVerification)
                    } else {
                        navController.navigate(RouteScanID( documentType = "driverLicence"))
                    }
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
                onClick = {navController.navigate(RouteHome)},
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }
    }
}