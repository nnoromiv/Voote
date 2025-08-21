package com.example.voote.view.scan.id

import android.content.Context
import android.net.Uri
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.firebase.data.Status
import com.example.voote.navigation.RouteAddressVerification
import com.example.voote.navigation.RouteDriverLicenceVerification
import com.example.voote.navigation.RouteStatus
import com.example.voote.navigation.toJson
import com.example.voote.utils.IdAnalyser
import com.example.voote.utils.helpers.handleCaptureIdSuspend
import com.example.voote.view.LoaderScreen
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.IdentityDetailsViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun IDPermissionGranted (context: Context, authManager: AuthViewModel, identityDetailsViewModel: IdentityDetailsViewModel, documentType: String, analyser: IdAnalyser, navController: NavController) {

    val isLoading = remember { mutableStateOf(false) }
    val lifeCycleOwner = LocalLifecycleOwner.current
    val executor = ContextCompat.getMainExecutor(context)
    val verification = Verification(authManager)
    val coroutineScope = rememberCoroutineScope()
    var lastCapturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val dataIsExtracted by identityDetailsViewModel.isExtracted.collectAsState()
    val passportData by identityDetailsViewModel.passportExtractedData.collectAsState()
    val driverLicenceData by identityDetailsViewModel.driverLicenceExtractedData.collectAsState()

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    val previewView = remember {
        PreviewView(context)
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    val cameraReady = remember { mutableStateOf(false) }

    fun handleUpload(imageUri: Uri) {
        coroutineScope.launch {

            val fileName = documentType + "Image"
            val result = verification.uploadImage(imageUri, fileName)

            Log.d("UPLOAD_IMAGE", result.toString())

            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()

            isLoading.value = false

            if(result.status == Status.ERROR) {

                navController.navigate(RouteStatus(
                    status = result.status,
                    nextScreen = ""
                ))

                return@launch
            }

            if(documentType == "passport"){
                identityDetailsViewModel.clearPassportData()
                navController.navigate(RouteStatus(
                    status = result.status,
                    nextScreen = RouteDriverLicenceVerification.toJson()
                ))
            } else if (documentType == "driverLicence") {
                identityDetailsViewModel.clearDriverLicenceData()
                navController.navigate(RouteStatus(
                    status = result.status,
                    nextScreen = RouteAddressVerification.toJson()
                ))
            }

        }
    }

    LaunchedEffect(dataIsExtracted) {
        if(dataIsExtracted) {
            lastCapturedImageUri.let { uri ->
                Log.d("ImageCaptured", uri.toString())

                if(uri != null) {
                    handleUpload(uri)
                }
            }
        }
    }

    LaunchedEffect(passportData, driverLicenceData) {
        cameraReady.value = false
    }

    fun handleCapture() {
        coroutineScope.launch {
            val isReady = cameraReady.value && analyser.isIDinBox.value

            if(!isReady) {
                return@launch
            }

            val imageUri = handleCaptureIdSuspend(imageCapture, executor, analyser, context)

            if(imageUri == null) {
                return@launch
            }

            lastCapturedImageUri = imageUri
            Toast.makeText(context, "Image Captured", Toast.LENGTH_SHORT).show()

            isLoading.value = true
        }

    }

    if(isLoading.value) {
        LoaderScreen()
    } else {
        IDCameraView(
            previewView,
            onClick = { handleCapture() },
            analyser,
            cameraReady
        )
    }

    // Launch camera preview
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
                        analyser.analyzeBitmap(bitmap)
                        handleCapture()
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