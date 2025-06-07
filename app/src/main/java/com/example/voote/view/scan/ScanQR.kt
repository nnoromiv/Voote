package com.example.voote.view.scan

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.voote.utils.QRCodeAnalyser
import com.example.voote.utils.helpers.RequestCameraPermission
import com.example.voote.view.scan.qr.QrPermissionGranted

@Composable
fun ScanQR(){
    val context = LocalContext.current

    val qrAnalyser = QRCodeAnalyser (
        onQrCodeScanned = { result ->
            Toast.makeText(context, "Scanned: $result", Toast.LENGTH_LONG).show()
            val browserIntent = Intent(Intent.ACTION_VIEW, result.toUri())
            context.startActivity(browserIntent)
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

