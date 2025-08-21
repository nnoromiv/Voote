package com.example.voote.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.example.voote.ThisApplication
import com.example.voote.model.data.FaceLivelinessResult
import com.example.voote.utils.helpers.vibratePhone
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FaceAnalyser (  private val context: Context,  private val onFaceAnalysed: (FaceLivelinessResult) -> Unit) {

    val isFaceInBox = mutableStateOf(false)
    private var lastFeedbackTime = 0L
    val coroutineScope = (context.applicationContext as ThisApplication).appScope

    private val recognizer = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) // Landmarks (eyes, mouth, nose)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // Needed for smile & eye open probabilities
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL) // Optional, for face shape
            .enableTracking()
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    fun analyzeBitmap(bitmap: Bitmap, onlyDetectBox: Boolean = false) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { faces ->
                val (detected, liveliness) = detectFaceAndLiveliness(faces, bitmap, onlyDetectBox)

                isFaceInBox.value = true

                if (detected && !onlyDetectBox && shouldTriggerFeedback()) {

                    coroutineScope.launch {
                        delay(2000)


                        if(liveliness == null) {
                            withContext(Dispatchers.Main){
                                Toast.makeText(context, "Uncertain face", Toast.LENGTH_SHORT).show()
                            }
                            return@launch
                        }

                        vibratePhone(context)
                        onFaceAnalysed(liveliness)
                    }
                }
            }
            .addOnFailureListener{
                isFaceInBox.value = false
            }
            .addOnCompleteListener {
                bitmap.recycle()
            }

    }

    private fun detectFaceAndLiveliness( faces: List<Face>, bitmap: Bitmap, onlyDetectBox: Boolean): Pair<Boolean, FaceLivelinessResult?> {
        val imageWidth = bitmap.width.toFloat()
        val imageHeight = bitmap.height.toFloat()

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        val scaleX = screenWidth / imageWidth
        val scaleY = screenHeight / imageHeight


        val faceTargetBox = Rect(
            screenWidth / 2 - 300,
            100f,
            screenWidth / 2 + 300,
            100f + 1440
        )

        for (face in faces) {
            val bounds = face.boundingBox

            val centerX = bounds.exactCenterX() * scaleX
            val centerY = bounds.exactCenterY() * scaleY
            val offset = Offset(centerX, centerY)

            if (faceTargetBox.contains(offset)) {
                return if (onlyDetectBox) {
                    Pair(true, FaceLivelinessResult())
                } else {
                    Pair(true, detectLiveliness(face))
                }
            }
        }

        return Pair(false, FaceLivelinessResult())
    }

    private fun shouldTriggerFeedback(): Boolean {
        val now = System.currentTimeMillis()

        return if(now - lastFeedbackTime > 2000) {
            lastFeedbackTime = now
            true
        } else {
            false
        }
    }

    private var lastLeftEyeOpen = true
    private var lastRightEyeOpen = true

    private fun detectBlink(face: Face): Boolean {
        val leftOpen = (face.leftEyeOpenProbability ?: 1f) > 0.5
        val rightOpen = (face.rightEyeOpenProbability ?: 1f) > 0.5

        val blinked = (lastLeftEyeOpen && !leftOpen) || (lastRightEyeOpen && !rightOpen)

        lastLeftEyeOpen = leftOpen
        lastRightEyeOpen = rightOpen

        return blinked
    }

    private fun detectLiveliness(face: Face): FaceLivelinessResult? {
        val details = FaceLivelinessResult()

        fun probabilityToYesNo(prob: Float?): String {
            return when {
                prob == null || prob < 0f -> "Unknown"
                prob > 0.5 -> "Yes"
                else -> "No"
            }
        }

        details.smile = probabilityToYesNo(face.smilingProbability)
        details.leftEyeOpen = probabilityToYesNo(face.leftEyeOpenProbability)
        details.rightEyeOpen = probabilityToYesNo(face.rightEyeOpenProbability)

        details.headEulerY = face.headEulerAngleY
        details.headEulerZ = face.headEulerAngleZ

        face.trackingId?.let { details.trackingId }

        val blinked = detectBlink(face)

        val isLive = (
                (face.smilingProbability ?: -1f) > 0.5 ||
                        ((face.leftEyeOpenProbability ?: -1f) > 0.5 && (face.rightEyeOpenProbability ?: -1f) > 0.5) ||
                        blinked
                )

        details.isLive = if (isLive) "Yes" else "Uncertain"

        if(details.isLive == "Uncertain") {
            return null
        }

        return details
    }

}
