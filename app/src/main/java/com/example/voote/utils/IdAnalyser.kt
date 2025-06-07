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
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IdAnalyser (
    private val context: Context,
    private val onIdDetected: (Map<String, String>) -> Unit,
) {
    val isIDinBox = mutableStateOf(false)
    private var lastFeedbackTime = 0L
    private var detectionJob: Job? = null

    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    @OptIn(ExperimentalGetImage::class)
    fun analyzeBitmap(bitmap: Bitmap, onlyDetectBox: Boolean = false) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        val imageWidth = bitmap.width
        val imageHeight = bitmap.height

        recognizer.process(inputImage)
            .addOnSuccessListener { text ->
                val (detected, extractedFields) = detectIDAndExtract(text, imageWidth, imageHeight, onlyDetectBox)

                isIDinBox.value = detected

                if (detected && !onlyDetectBox && shouldTriggerFeedback()) {

                    detectionJob?.cancel()

                    detectionJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(700)
                        vibratePhone(context)
                        onIdDetected(extractedFields)
                    }
                }
            }
            .addOnFailureListener{
                Log.e("ImageCapture", "Scanning failed", it)
                isIDinBox.value = false
            }
            .addOnCompleteListener {
                bitmap.recycle()
            }

    }

    private fun detectIDAndExtract(
        text: Text,
        imageWidth: Int,
        imageHeight: Int,
        onlyDetectBox: Boolean
    ): Pair<Boolean, Map<String, String>> {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        val idTargetBox = Rect(
            screenWidth / 2 - 350,
            screenHeight / 2 - 300,
            screenWidth / 2 + 300,
            screenHeight / 2 + 350
        )

        val scaleX = screenWidth / imageWidth
        val scaleY = screenHeight / imageHeight

        for (block in text.textBlocks) {
            val rect = block.boundingBox ?: continue
            val centerX = rect.exactCenterX() * scaleX
            val centerY = rect.exactCenterY() * scaleY
            val offset = Offset(centerX, centerY)

            if (idTargetBox.contains(offset)) {
                return if (onlyDetectBox) {
                    true to emptyMap()
                } else {
                    true to extractPassportFields(block.text)
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
        isIDinBox.value = false
    }

    private fun extractPassportFields(text: String): Map<String, String> {
        Log.d("ImageCapture", text)
        val details = mutableMapOf<String, String>()

        val passportNumberRegex = Regex("\\b([A-Z]\\d{7,8})\\b")
        val nameRegex = Regex("P<\\w{3}<<([A-Z<]+)<<([A-Z<]+)")

        passportNumberRegex.find(text)?.let {
            details["PassportNumber"] = it.value
        }

        nameRegex.find(text)?.let {
            details["FirstName"] = it.groupValues[1].replace("<", " ").trim()
            details["LastName"] = it.groupValues[2].replace("<", " ").trim()
        }

        if (details.isNotEmpty()) {
            cancelDetectionJob()
            isIDinBox.value = false
        }

        return details
    }

}
