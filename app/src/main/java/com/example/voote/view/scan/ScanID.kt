package com.example.voote.view.scan

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.firebase.data.STATUS
import com.example.voote.model.data.DriverLicenceExtractedData
import com.example.voote.utils.helpers.RequestCameraPermission
import com.example.voote.utils.IdAnalyser
import com.example.voote.view.scan.id.IDPermissionGranted
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.IdentityDetailsViewModel
import com.example.voote.viewModel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ScanID(authManager: AuthViewModel, documentType: String, identityDetailsViewModel: IdentityDetailsViewModel, userViewModel: UserViewModel, navController: NavController){
    val context = LocalContext.current
    val verification = Verification(authManager)
    val coroutineScope = rememberCoroutineScope()
    val user = userViewModel.userData.collectAsState()

    val analyser = IdAnalyser(
        context,
        documentType,
        onPassportDetected = { result ->

            Log.d("ImageCaptured", "Scanned: $result")

            identityDetailsViewModel.setPassportExtractedData(result)

            coroutineScope.launch {
                try {
                    val uploadResult = verification.uploadPassportDataToFirebase(result)

                    if (uploadResult.status == STATUS.ERROR) {
                        Toast.makeText(
                            context,
                            "Error: ${uploadResult.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("ImageCaptured", "Error uploading: ${uploadResult.message}")
                    } else {
                        Toast.makeText(
                            context,
                            "Passport data uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ImageCaptured", "Scanned: $result")
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Unexpected error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ImageCaptured", "Upload crash", e)
                } finally {
                    // ✅ mark extraction complete ONLY after upload finishes
                    identityDetailsViewModel.setIsExtracted(true)
                }
            }
        },
        onDriverLicenceDetected = { result ->

            val uploadData = result?.copy(
                surname = result.surname.ifEmpty { user.value?.lastName ?: "" },
                firstname = result.firstname.ifEmpty { user.value?.firstName ?: "" }
            ) ?: DriverLicenceExtractedData(
                surname = user.value?.lastName ?: "",
                firstname = user.value?.firstName ?: "",
            )

            Log.d("ImageCaptured", "Scanned: $uploadData")

            identityDetailsViewModel.setDriverLicenceExtractedData(uploadData)

            coroutineScope.launch {
                try {
                    val uploadResult = verification.uploadDriverLicenceDataToFirebase(uploadData)

                    if (uploadResult.status == STATUS.ERROR) {
                        Toast.makeText(
                            context,
                            "Error: ${uploadResult.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("ImageCaptured", "Error uploading: ${uploadResult.message}")
                    } else {
                        Toast.makeText(
                            context,
                            "Licence data uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("ImageCaptured", "Scanned: $result")
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Unexpected error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ImageCaptured", "Upload crash", e)
                } finally {
                    // ✅ mark extraction complete ONLY after upload finishes
                    identityDetailsViewModel.setIsExtracted(true)
                }
            }
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
}



