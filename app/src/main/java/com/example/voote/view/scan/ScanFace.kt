package com.example.voote.view.scan

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.voote.firebase.data.AppResult
import com.example.voote.utils.FaceAnalyser
import com.example.voote.utils.helpers.RequestCameraPermission
import com.example.voote.utils.helpers.rotateBitmap
import com.example.voote.utils.helpers.saveBitmapToFile
import com.example.voote.view.scan.face.FacePermissionGranted
import java.util.concurrent.Executor

@Composable
fun ScanFace(navController: NavController){
    val context = LocalContext.current

    val analyser = FaceAnalyser(
        context,
        onFaceAnalysed = { result ->
            Log.d("FACE_CAPTURE", "Scanned: $result")
        },
    )

    RequestCameraPermission(
        onCameraPermissionGranted = {
            FacePermissionGranted(
                navController,
                context,
                analyser,
            )
        },

        onCameraPermissionDenied = {
            PermissionDeniedScreen()
        }
    )
}

fun faceCapture(
    imageCapture: ImageCapture,
    executor: Executor,
    analyser: FaceAnalyser,
    context: Context,
    onImageSaved: (Uri?) -> Unit = {}
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val originalBitmap = image.toBitmap()
                val rotatedBitmap = rotateBitmap(originalBitmap, 270f)

                // Analyse face from rotated bitmap
                analyser.analyzeBitmap(rotatedBitmap)

                val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()

                // Target box in screen coordinates
                val faceTargetBox = Rect(
                    screenWidth / 2 - 300,
                    100f,
                    screenWidth / 2 + 300,
                    100f + 2040
                )

                // Scaling factors for rotated bitmap
                val scaleX = rotatedBitmap.width / screenWidth
                val scaleY = rotatedBitmap.height / screenHeight

                // Map screen box → rotated bitmap coordinates
                val cropRect = Rect(
                    faceTargetBox.left * scaleX,
                    faceTargetBox.top * scaleY,
                    faceTargetBox.right * scaleX,
                    faceTargetBox.bottom * scaleY
                )

                // Ensure rect is inside bounds
                val safeCropRect = Rect(
                    cropRect.left.coerceIn(0f, rotatedBitmap.width.toFloat()),
                    cropRect.top.coerceIn(0f, rotatedBitmap.height.toFloat()),
                    cropRect.right.coerceIn(0f, rotatedBitmap.width.toFloat()),
                    cropRect.bottom.coerceIn(0f, rotatedBitmap.height.toFloat())
                )

                val savedImageUri = saveBitmapToFile(
                    rotatedBitmap,
                    context,
                    safeCropRect,
                    "face_${System.currentTimeMillis()}.jpg"
                )

                if (savedImageUri is AppResult.Error) {
                    Log.e("ImageCapture", "Error saving image: ${savedImageUri.message}")
                    onImageSaved(null)
                    return
                }

                onImageSaved(savedImageUri.data)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onImageSaved(null)
                Log.e("FaceCapture", "Error: ${exception.message}")
            }
        }
    )
}
