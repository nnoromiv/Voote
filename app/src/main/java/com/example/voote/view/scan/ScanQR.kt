package com.example.voote.view.scan

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.voote.utils.QRCodeAnalyser
import com.example.voote.utils.helpers.RequestCameraPermission
import com.example.voote.utils.helpers.copyToClipboard
import com.example.voote.utils.helpers.isValidBlockchainAddress
import com.example.voote.view.scan.qr.QrPermissionGranted

@Composable
fun ScanQR(){
    val context = LocalContext.current
    val isScanned = remember { mutableStateOf(false) }

    val qrAnalyser = QRCodeAnalyser (
        onQrCodeScanned = { result ->
            val isValid = isValidBlockchainAddress(result)

            if(!isValid) {
                Toast.makeText(context, "Invalid address", Toast.LENGTH_LONG).show()
                return@QRCodeAnalyser
            }

            if(isScanned.value) return@QRCodeAnalyser

            copyToClipboard(context, "address", result)

            isScanned.value = true
        }
    )

    RequestCameraPermission(
        onCameraPermissionGranted = {
            QrPermissionGranted (
                analyser = qrAnalyser
            )
        },

        onCameraPermissionDenied = {
            PermissionDeniedScreen()
        }
    )
}

