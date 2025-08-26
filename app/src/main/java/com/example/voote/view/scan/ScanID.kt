package com.example.voote.view.scan

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.model.data.DriverLicenceExtractedData
import com.example.voote.model.data.PassportExtractedData
import com.example.voote.utils.helpers.RequestCameraPermission
import com.example.voote.utils.IdAnalyser
import com.example.voote.view.scan.id.IDPermissionGranted
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.IdentityDetailsViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel

@Composable
fun ScanID(authManager: AuthViewModel, documentType: String, identityDetailsViewModel: IdentityDetailsViewModel, userViewModel: UserViewModel, kycViewModel: KycViewModel, navController: NavController){

    val context = LocalContext.current
    val verification = Verification(authManager)

    val user by userViewModel.userData.collectAsState()
    val kyc by kycViewModel.kycData.collectAsState()
    val isUploading by identityDetailsViewModel.isUploading.collectAsState()
    val uploadMessage by identityDetailsViewModel.uploadMessage.collectAsState()

    LaunchedEffect(Unit) {
        identityDetailsViewModel.setPassportExtractedData(PassportExtractedData())
        identityDetailsViewModel.setDriverLicenceExtractedData(DriverLicenceExtractedData())
        identityDetailsViewModel.setIsExtracted(false)
    }

    val analyser = IdAnalyser(
        context,
        documentType,
        onPassportDetected = { result ->

            val uploadData = result.copy(
                lastName = result.lastName.ifEmpty { user?.lastName ?: "" },
                givenNames = result.givenNames.ifEmpty { user?.firstName ?: "" },
                passportNumber = result.passportNumber.ifEmpty { kyc?.passportNumber ?: "" },
                expiryDate = result.expiryDate.ifEmpty { kyc?.passportExpiryDate ?: "" },
            )

            identityDetailsViewModel.setPassportExtractedData(uploadData)
            Log.d("ID_DEBUG", "Passport data: $uploadData")
            identityDetailsViewModel.uploadPassportData(uploadData, verification, context)

        },
        onDriverLicenceDetected = { result ->

            val uploadData = result?.copy(
                surname = result.surname.ifEmpty { user?.lastName ?: "" },
                firstname = result.firstname.ifEmpty { user?.firstName ?: "" },
                licenceNumber = result.licenceNumber.ifEmpty { kyc?.driverLicenceNumber ?: "" },
                expiryDate = result.expiryDate.ifEmpty { kyc?.driverLicenceExpiryDate ?: "" },
            ) ?: DriverLicenceExtractedData(
                surname = user?.lastName ?: "",
                firstname = user?.firstName ?: "",
                licenceNumber = kyc?.driverLicenceNumber ?: "",
                expiryDate = kyc?.driverLicenceExpiryDate ?: ""
            )

            identityDetailsViewModel.setDriverLicenceExtractedData(uploadData)
            identityDetailsViewModel.uploadDriverLicenceData(uploadData, verification, context)
        },
        navController
    )

    RequestCameraPermission(
        onCameraPermissionGranted = {
            IDPermissionGranted(
                context,
                authManager,
                identityDetailsViewModel,
                documentType,
                analyser,
                navController
            )
        },

        onCameraPermissionDenied = {
            PermissionDeniedScreen()
        }
    )

    if (isUploading) {
        Toast.makeText(context, "Uploading document...", Toast.LENGTH_SHORT).show()
    } else if (uploadMessage != null) {
        Log.d("ScanID", "Upload status: $uploadMessage")
    }
}



