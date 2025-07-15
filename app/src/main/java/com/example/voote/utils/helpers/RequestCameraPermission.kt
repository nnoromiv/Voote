package com.example.voote.utils.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission(onCameraPermissionGranted: @Composable () -> Unit, onCameraPermissionDenied: @Composable () -> Unit) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    when {
        cameraPermissionState.status.isGranted -> {
            onCameraPermissionGranted()
            // Permission has been granted, show the camera preview
        }

        cameraPermissionState.status.shouldShowRationale -> {
            onCameraPermissionDenied()
        }

        else -> {
            onCameraPermissionDenied()
        }
    }
}