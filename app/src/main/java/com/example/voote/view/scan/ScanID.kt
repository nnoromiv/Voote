package com.example.voote.view.scan

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.voote.utils.helpers.RequestCameraPermission
import com.example.voote.utils.IdAnalyser
import com.example.voote.view.scan.id.IDPermissionGranted
import com.example.voote.viewModel.AuthViewModel

@Composable
fun ScanID(authManager: AuthViewModel, documentType: String, navController: NavController){
    val context = LocalContext.current

    val analyser = IdAnalyser(
        context,
        onIdDetected = { result ->
            Log.d("ImageCapture", "Scanned: $result")
        },
    )

    RequestCameraPermission(
        onCameraPermissionGranted = {
            IDPermissionGranted(
                context,
                authManager,
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




