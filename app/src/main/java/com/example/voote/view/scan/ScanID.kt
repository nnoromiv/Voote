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
import com.example.voote.utils.helpers.RequestCameraPermission
import com.example.voote.utils.IdAnalyser
import com.example.voote.utils.helpers.saveBitmapToFile
import com.example.voote.view.scan.id.IDPermissionGranted
import java.util.concurrent.Executor

@Composable
fun ScanID(navController: NavController, documentType: String){
    val context = LocalContext.current

    val analyser = IdAnalyser(
        context,
        onIdDetected = { result ->
            Log.d("ImageCapture", "Scanned: $result")
        },
    )

    RequestCameraPermission(
        onCameraPermissionGranted = {
            IDPermissionGranted(
                documentType,
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


fun handleCaptureId(
    imageCapture: ImageCapture,
    executor: Executor,
    analyser: IdAnalyser,
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

                val idTargetBox = Rect(
                    screenWidth / 2 - 250,
                    screenHeight / 2 - 500,
                    screenWidth / 2 + 250,
                    screenHeight / 2 + 500
                )

                val scaleX = bitmap.width / screenWidth
                val scaleY = bitmap.height / screenHeight

                val cropRect = Rect(
                    idTargetBox.left * scaleX,
                    idTargetBox.top * scaleY,
                    idTargetBox.right * scaleX,
                    idTargetBox.bottom * scaleY
                )

                val savedImageUri = saveBitmapToFile(bitmap, context, cropRect, "id_${System.currentTimeMillis()}.jpg")
                onImageSaved(savedImageUri)

                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                onImageSaved(null)
                Log.e("ImageCapture", "Error: ${exception.message}")
            }

        }
    )
}



