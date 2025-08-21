package com.example.voote.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.navigation.NavController
import com.example.voote.ThisApplication
import com.example.voote.firebase.data.Status
import com.example.voote.model.data.DriverLicenceExtractedData
import com.example.voote.model.data.PassportExtractedData
import com.example.voote.model.data.hasAnyField
import com.example.voote.navigation.RouteScanID
import com.example.voote.navigation.RouteStatus
import com.example.voote.navigation.toJson
import com.example.voote.utils.helpers.vibratePhone
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdAnalyser (
    private val context: Context,
    private val documentType: String,
    private val onPassportDetected: (PassportExtractedData) -> Unit,
    private val onDriverLicenceDetected: (DriverLicenceExtractedData?) -> Unit,
    val navController: NavController
    )
{
    val isIDinBox = mutableStateOf(false)
    private var lastFeedbackTime = 0L

    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    val coroutineScope = (context.applicationContext as ThisApplication).appScope

    @OptIn(ExperimentalGetImage::class)
    fun analyzeBitmap(bitmap: Bitmap, onlyDetectBox: Boolean = false) {

        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val triesFailed = mutableIntStateOf(0)


        recognizer.process(inputImage)
            .addOnSuccessListener { text ->
                when (documentType) {
                    "passport" -> {
                        val (detected, extractedFields) = detectPassportTextAndExtract(text, inputImage, onlyDetectBox)
                        isIDinBox.value = detected

                        coroutineScope.launch {
                            delay(1000)

                            if (detected && !onlyDetectBox && shouldTriggerFeedback()) {
                                if(extractedFields == null) {
                                    triesFailed.intValue++

                                    if(triesFailed.intValue >= 10) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "No passport number found", Toast.LENGTH_SHORT).show()

                                            navController.navigate(
                                                RouteStatus(
                                                    status = Status.ERROR,
                                                    nextScreen = RouteScanID("passport").toJson()
                                                )
                                            )
                                        }
                                    } else {
                                        Log.e(TAG, "No passport number found $triesFailed")
                                    }
                                } else {
                                    vibratePhone(context)
                                    onPassportDetected(extractedFields)
                                }
                            }
                        }
                    }
                    "driverLicence" -> {
                        val (detected, extractedFields) = detectDriverLicenceAndExtract(text, inputImage, onlyDetectBox)
                        isIDinBox.value = detected

                        coroutineScope.launch {
                            delay(1000)

                            if (detected && !onlyDetectBox && shouldTriggerFeedback()) {
                                vibratePhone(context)
                                onDriverLicenceDetected(extractedFields)
                            }
                        }
                    }
                    else -> {}
                }
            }
            .addOnFailureListener{
                Log.e(TAG,"Scanning failed", it)
                isIDinBox.value = false
            }
            .addOnCompleteListener {
                bitmap.recycle()
            }

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

    private fun detectPassportTextAndExtract( text: Text, inputImage: InputImage, onlyDetectBox: Boolean ): Pair<Boolean, PassportExtractedData?> {
        val (scaleX, scaleY, idTargetBox) = getScalingAndBox(inputImage.width, inputImage.height)

        for (block in text.textBlocks) {
            val offset = block.centerOffsetScaled(scaleX, scaleY)
            if(!idTargetBox.contains(offset)) continue

            if(onlyDetectBox) {
                return Pair(true, PassportExtractedData())
            }

            val blockText = block.text.trim().replace("«", "<")

            if (!blockText.contains("<") || blockText.length < 40) {
                Log.e(TAG, "No MRZ found in this block")
            }

            val mergedMrz = if (blockText.length >= 88) {
                // Already two lines
                blockText.chunked(44).joinToString("\n")
            } else {
                // Single long MRZ line
                blockText.chunked(44).joinToString("\n")
            }

            return Pair(true, extractPassportFields(mergedMrz))
        }

        return Pair(false, PassportExtractedData())
    }

    private fun detectDriverLicenceAndExtract( text: Text, inputImage: InputImage, onlyDetectBox: Boolean ): Pair<Boolean, DriverLicenceExtractedData?> {
        val (scaleX, scaleY, finalBox) = getScalingAndBox(inputImage.width, inputImage.height)

        for (block in text.textBlocks) {
            val offset = block.centerOffsetScaled(scaleX, scaleY)
            if(!finalBox.contains(offset)) continue


            if(onlyDetectBox) {
                return Pair(true, DriverLicenceExtractedData())
            }

            val mrzBlocks = block.text

            return Pair(true, extractDriverLicenceFields(mrzBlocks))
        }

        return Pair(false, DriverLicenceExtractedData())
    }

    private fun getScalingAndBox(imageWidth: Int, imageHeight: Int, isForLicenceBox: Boolean = false): Triple<Float, Float, Rect> {

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        val idTargetBox = Rect(
            screenWidth / 2 - 210,
            screenHeight / 2 - 250,
            screenWidth / 2 + 210,
            screenHeight / 2 + 250
        )

        val scaleX = screenWidth / imageWidth
        val scaleY = screenHeight / imageHeight

        val bitmapBox = RectF(
            idTargetBox.left / scaleX,
            idTargetBox.top / scaleY,
            idTargetBox.right / scaleX,
            idTargetBox.bottom / scaleY
        )

        val licenceBox = Rect(
            (bitmapBox.left + (bitmapBox.width() / 4)),
            (bitmapBox.top + (bitmapBox.height() / 45)),
            (bitmapBox.right - (bitmapBox.width() / 4)),
            (bitmapBox.bottom - (bitmapBox.height() / 4))
        )

        val finalBox = if (isForLicenceBox) licenceBox else idTargetBox

        return Triple(scaleX, scaleY, finalBox)
    }

    private fun Text.TextBlock.centerOffsetScaled(scaleX: Float, scaleY: Float): Offset {
        val rect = boundingBox ?: return Offset.Zero
        val centerX = rect.exactCenterX() * scaleX
        val centerY = rect.exactCenterY() * scaleY
        return Offset(centerX, centerY)
    }

    private fun extractPassportFields(text: String): PassportExtractedData? {

        val details = PassportExtractedData()

        // Merge lines and replace strange chars
        var mrz = text
            .replace("\n", "")
            .replace("«", "<")
            .replace(" ", "")
            .replace(">", "<") // unify > into <
            .replace(Regex("(?i)c{2,}"), "<") // collapse 2+ C/c into a single <
            .uppercase()
            .trim()

        if (mrz.length >= 2 && mrz[1] != '<') {
            mrz = mrz[0] + "<" + mrz.substring(2)
        }

        val mrzRegex = Regex("([A-Z0-9<]{44})([A-Z0-9<]{44})")
        val match = mrzRegex.find(mrz)

        if (match == null) {
            Log.e(TAG, "Could not find valid MRZ in: $match, $mrz")
            return details
        }

        val line1 = mrz.substring(0, 44)
        val line2 = mrz.substring(44)

        try {
            details.countryCode = line1.substring(2, 5)
            details.lastName = line1.substring(5, line1.indexOf("<<")).replace("<", " ")
            details.givenNames = line1.substring(line1.indexOf("<<") + 2).replace("<", " ")
            details.passportNumber = line2.substring(0, 9)
            details.nationality = line2.substring(10, 13)
            details.dateOfBirth = line2.substring(13, 19)
            details.sex = when (line2[20]) { 'M' -> "Male"; 'F' -> "Female"; else -> "Unspecified" }
            details.expiryDate = line2.substring(21, 27)
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting fields", e)
        }

        if(!details.passportNumber.isEmpty()) {
            return null
        }

        Log.d("ImageCapture", "Extracted: $details")

        return details
    }

    private fun extractDriverLicenceFields(text: String): DriverLicenceExtractedData? {
        val details = DriverLicenceExtractedData()

        try {
            // 1. Surname
            details.surname = Regex("""1\.\s*([A-Z\s\-]+)""")
                .find(text)?.groupValues?.get(1)?.trim() ?: ""

            // 2. Firstname (strip titles MR/MRS/MS etc.)
            details.firstname = Regex("""2\.\s*(.*)""")
                .find(text)?.groupValues?.get(1)
                ?.replace(Regex("^(MR|MRS|MS|MISS)+", RegexOption.IGNORE_CASE), "")
                ?.trim() ?: ""

            // 3. DOB (first date found in text)
            details.dob = Regex("""\b\d{2}[./-]\d{2}[./-]\d{4}\b""")
                .find(text)?.value ?: ""

            // 5. Licence number (alphanumeric DVLA format)
            details.licenceNumber = Regex("""5\.\s*([A-Z0-9]+)""")
                .find(text)?.groupValues?.get(1)?.trim() ?: ""

            // 8. Address
            details.address = Regex("""8\.\s*(.*)""")
                .find(text)?.groupValues?.get(1)?.trim() ?: ""

        } catch (e: Exception) {
            Log.e(TAG, "Error extracting fields", e)
        }

        if(!details.hasAnyField()) {
            return null
        }

        return details
    }

    companion object {
        private const val TAG = "ID_DEBUG"
    }

}
