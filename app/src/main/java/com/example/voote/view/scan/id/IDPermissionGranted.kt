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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.navigation.AddressVerification
import com.example.voote.navigation.DriverLicenceVerification
import com.example.voote.utils.IdAnalyser
import com.example.voote.utils.helpers.generateHMAC
import com.example.voote.utils.helpers.getOrCreateHMACKey
import com.example.voote.utils.helpers.verifyHMAC
import com.example.voote.view.scan.handleCaptureId
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.WalletViewModel
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

    val secretKey = getOrCreateHMACKey()

    val authViewModel : AuthViewModel = viewModel()
    val walletViewModel : WalletViewModel = viewModel()

    val uid = authViewModel.userUid().toString()

    val address = walletViewModel.address
    val walletId = generateHMAC(uid + address, secretKey)

    val isValid = verifyHMAC(uid + address, walletId, secretKey)

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
                        imageUri ->
                    if (imageUri != null) {
                        if(isValid) {
                            val imageToSave = documentType + "Image"
                            Verification().uploadImage(imageUri, fileName = documentType, imageToSave) {
                                onError -> Log.e("ImageCapture", "Error uploading image: ${onError.message}")
                            }

                            Toast.makeText(context, "Image saved", Toast.LENGTH_LONG).show()
                            when (documentType) {
                                "passport" -> navController.navigate(DriverLicenceVerification)
                                "driverLicence" -> navController.navigate(AddressVerification)
                                else -> Log.w("Navigation", "Unknown documentType: $documentType")
                            }
                        } else {
                            Log.d("ImageCapture", "Image not saved")
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