package com.example.voote.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.example.voote.utils.helpers.vibratePhone
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FaceAnalyser (
    private val context: Context,
    private val onFaceAnalysed: (Map<String, String>) -> Unit,
) {

    val isFaceInBox = mutableStateOf(false)
    private var lastFeedbackTime = 0L
    private var detectionJob: Job? = null

    private val recognizer = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .enableTracking()
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    fun analyzeBitmap(bitmap: Bitmap, onlyDetectBox: Boolean = false) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { faces ->
                val (detected, liveliness) = detectFaceAndLiveliness(faces, bitmap, onlyDetectBox)

                isFaceInBox.value = detected

                if (detected && !onlyDetectBox && shouldTriggerFeedback()) {

                    detectionJob?.cancel()

                    detectionJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(700)
                        vibratePhone(context)
                        onFaceAnalysed(liveliness)
                    }
                }
            }
            .addOnFailureListener{
                Log.e("FaceAnalyser", "Scanning failed", it)
                isFaceInBox.value = false
            }
            .addOnCompleteListener {
                bitmap.recycle()
            }

    }

    private fun detectFaceAndLiveliness(
        faces: List<Face>,
        bitmap: Bitmap,
        onlyDetectBox: Boolean
    ): Pair<Boolean, Map<String, String>> {
        val imageWidth = bitmap.width.toFloat()
        val imageHeight = bitmap.height.toFloat()

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        val scaleX = screenWidth / imageWidth
        val scaleY = screenHeight / imageHeight


        val faceTargetBox = Rect(
            screenWidth / 2 - 280,
            screenHeight / 2 - 300,
            screenWidth / 2 + 300,
            screenHeight / 2 + 280
        )

        for (face in faces) {
            val bounds = face.boundingBox
            val centerX = bounds.exactCenterX() * scaleX
            val centerY = bounds.exactCenterY() * scaleY
            val offset = Offset(centerX, centerY)

            Log.d("FaceAnalyser", "Target Box: $faceTargetBox, Offset: $offset")

            if (faceTargetBox.contains(offset)) {
                return if (onlyDetectBox) {
                    true to emptyMap()
                } else {
                    true to detectLiveliness(face)
                }
            }

        }

        return false to emptyMap()
    }

    private fun shouldTriggerFeedback(): Boolean {
        val now = System.currentTimeMillis()

        return if(now - lastFeedbackTime > 3000) {
            lastFeedbackTime = now
            true
        } else {
            false
        }
    }

    private fun cancelDetectionJob() {
        detectionJob?.cancel()
        detectionJob = null
        isFaceInBox.value = false
    }

    private fun detectLiveliness(face: Face): Map<String, String> {
        val details = mutableMapOf<String, String>()

        // Smile probability (0.0 to 1.0)
        face.smilingProbability?.let { probability ->
            details["Smile"] = if (probability > 0.5) "Yes" else "No"
        }

        // Eye openness probability (left and right)
        face.leftEyeOpenProbability?.let { probability ->
            details["LeftEyeOpen"] = if (probability > 0.5) "Yes" else "No"
        }

        face.rightEyeOpenProbability?.let { probability ->
            details["RightEyeOpen"] = if (probability > 0.5) "Yes" else "No"
        }

        // Head orientation (Euler angles)
        details["HeadEulerY"] = "%.2f".format(face.headEulerAngleY) // left-right rotation
        details["HeadEulerZ"] = "%.2f".format(face.headEulerAngleZ) // tilt

        // Add tracking ID if available
        face.trackingId?.let {
            details["TrackingId"] = it.toString()
        }

        // Optional: Mark as live if any condition is confidently met
        val isLive = ((face.smilingProbability ?: 0f) > 0.5) ||
                ((face.leftEyeOpenProbability ?: 0f) > 0.5 && (face.rightEyeOpenProbability
                    ?: 0f) > 0.5)
        details["IsLive"] = if (isLive) "Yes" else "Uncertain"

        if (details.isNotEmpty()) {
            cancelDetectionJob()
            isFaceInBox.value = false
        }

        return details
    }

}
