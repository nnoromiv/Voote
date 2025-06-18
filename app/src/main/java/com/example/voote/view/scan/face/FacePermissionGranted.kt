package com.example.voote.view.scan.face

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
import com.example.voote.navigation.FaceVerification
import com.example.voote.utils.FaceAnalyser
import com.example.voote.view.scan.faceCapture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FacePermissionGranted(
    navController: NavController,
    context: Context,
    analyser: FaceAnalyser,
) {

    val cameraReady = remember { mutableStateOf(false) }
    val hasCaptured = remember { mutableStateOf(false) }

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

    FaceCameraView(
        navController,
        previewView,
        analyser,
    )

    LaunchedEffect(cameraReady.value) {
        if(!cameraReady.value) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

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
                Log.e("FaceCapture", "Error: ${e.message}")
                cameraReady.value = false
            }
        }
    }

    // Auto-capture effect when face is in box and ready
    LaunchedEffect(analyser.isFaceInBox.value, cameraReady.value) {
        if (cameraReady.value && analyser.isFaceInBox.value && !hasCaptured.value) {
            hasCaptured.value = true // Prevent re-capture

            faceCapture(imageCapture, executor, analyser, context) { uri ->
                if (uri != null) {
                    navController.navigate(FaceVerification(
                        userImageUri = uri.toString()
                    ))
                    Toast.makeText(context, "Face Captured!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("FaceCapture", "Image not saved")
                }
                // Optional delay before allowing new captures

                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    hasCaptured.value = false
                }

            }
        }
    }

}