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
import com.example.voote.utils.FaceAnalyser
import com.example.voote.utils.helpers.RequestCameraPermission
import com.example.voote.utils.helpers.saveBitmapToFile
import com.example.voote.view.scan.face.FacePermissionGranted
import java.util.concurrent.Executor

@Composable
fun ScanFace(navController: NavController){
    val context = LocalContext.current

    val analyser = FaceAnalyser(
        context,
        onFaceAnalysed = { result ->
            Log.d("FaceCapture", "Scanned: $result")
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

                val bitmap = image.toBitmap()
                analyser.analyzeBitmap(bitmap)

                val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()

                val faceTargetBox = Rect(
                    screenWidth / 2 - 400,
                    screenHeight / 2 - 600,
                    screenWidth / 2 + 400,
                    screenHeight / 2 + 600
                )

                val scaleX = bitmap.width / screenWidth
                val scaleY = bitmap.height / screenHeight

                val cropRect = Rect(
                    faceTargetBox.left * scaleX,
                    faceTargetBox.top * scaleY,
                    faceTargetBox.right * scaleX,
                    faceTargetBox.bottom * scaleY
                )

                val savedImageUri = saveBitmapToFile(bitmap, context, cropRect, "face_${System.currentTimeMillis()}.jpg")
                onImageSaved(savedImageUri)

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