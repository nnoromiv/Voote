package com.example.voote.view.scan.qr

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun QrPermissionGranted (
    analyser: ImageAnalysis.Analyzer
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val previewView = remember {
        PreviewView(context)
    }

    QrCameraView (
        previewView
    )

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val analyseImage = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    analyser
                )
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try{
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifeCycleOwner,
                cameraSelector,
                preview,
                analyseImage
            )

        } catch (e: Exception) {
            Log.e("ScanScreen", "Error: ${e.message}")
        }
    }
}