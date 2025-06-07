package com.example.voote.view.scan.id

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.voote.navigation.Address
import com.example.voote.navigation.DriverLicence
import com.example.voote.utils.IdAnalyser
import com.example.voote.view.scan.handleCaptureId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IDPermissionGranted(
    documentType: String,
    navController: NavController,
    context: Context,
    analyser: IdAnalyser,
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    val executor = ContextCompat.getMainExecutor(context)

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val previewView = remember {
        PreviewView(context)
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    val cameraReady = remember { mutableStateOf(false) }

    IDCameraView(
        navController,
        previewView,
        onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Keep Steady Capturing...", Toast.LENGTH_SHORT).show()
                delay(3000)
            }

            if(cameraReady.value && analyser.isIDinBox.value) {
                handleCaptureId(imageCapture, executor, analyser, context) {
                        uri ->
                    if (uri != null) {
                        Log.d("ImageCapture", "Image saved: $uri")
                        Toast.makeText(context, "Image saved", Toast.LENGTH_LONG).show()
                        when (documentType) {
                            "passport", "brp" -> navController.navigate(DriverLicence)
                            "DL" -> navController.navigate(Address)
                            else -> Log.w("Navigation", "Unknown documentType: $documentType")
                        }
                    } else {
                        Log.d("ImageCapture", "Image not saved")
                    }
                }
            } else {
                Toast.makeText(context, "Please align your passport", Toast.LENGTH_LONG).show()
            }
        },
        analyser,
        cameraReady
    )

    LaunchedEffect(cameraReady.value) {
        if(!cameraReady.value) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor) { imageProxy ->
                        val bitmap = imageProxy.toBitmap()
                        analyser.analyzeBitmap(bitmap, onlyDetectBox = true)
                        imageProxy.close()
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifeCycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )

                cameraReady.value = true
            } catch (e: Exception) {
                Log.e("ImageCapture", "Error: ${e.message}")
                cameraReady.value = false
            }
        }
    }

}