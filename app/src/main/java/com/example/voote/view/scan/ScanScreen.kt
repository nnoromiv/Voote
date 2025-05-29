package com.example.voote.view.scan

import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.utils.QRCodeAnalyser
import com.example.voote.utils.RequestCameraPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun ScanScreen(){
    RequestCameraPermission(
        onCameraPermissionGranted = {
            PermissionGrantedScreen()
        },

        onCameraPermissionDenied = {
            PermissionDeniedScreen()
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionDeniedScreen() {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    Scaffold {
            innerPadding ->
        Column (
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Component().Text(
                "Camera access is required to scan QR codes.",
                fontSize = 18
            )
            Spacer(
                modifier = Modifier.height(20.dp)
            )

            PrimaryButton(
                text = "Grant Permission",
                onClick = {
                    cameraPermissionState
                        .launchPermissionRequest()
                }
            )

        }
    }
}


@Composable
fun PermissionGrantedScreen() {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val previewView = remember {
        PreviewView(context)
    }

    Scaffold {
        innerPadding ->
        AndroidView(
            factory = {
                previewView
            },
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(0.dp)
        )
    }

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val locateQr = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QRCodeAnalyser {
                        qrResult -> Toast.makeText(context, "Scanned: $qrResult", Toast.LENGTH_LONG).show()
                    // You can navigate or update state here
                    }
                )
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try{
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifeCycleOwner,
                cameraSelector,
                preview,
                locateQr
            )

        } catch (e: Exception) {
            Log.e("ScanScreen", "Error: ${e.message}")
        }
    }
}