package com.example.voote.utils.helpers

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.util.Patterns
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.ui.geometry.Rect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voote.R
import com.example.voote.firebase.data.AppResult
import com.example.voote.utils.Constants
import com.example.voote.utils.IdAnalyser
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import org.web3j.crypto.MnemonicUtils
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.EncodeHintType
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.core.net.toUri
import com.example.voote.model.data.ElectionData
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun saveBitmapToFile(bitmap: Bitmap, context: Context, cropRect: Rect, fileName: String): AppResult<Uri> {
    return try {
        val saveLeft = cropRect.left.coerceIn(0f, bitmap.width.toFloat())
        val saveTop = cropRect.top.coerceIn(0f, bitmap.height.toFloat())
        val saveRight = cropRect.right.coerceIn(saveLeft, bitmap.width.toFloat())
        val saveBottom = cropRect.bottom.coerceIn(saveTop, bitmap.height.toFloat())

        val width = (saveRight - saveLeft).coerceAtLeast(1F)
        val height = (saveBottom - saveTop).coerceAtLeast(1F)
        val croppedBitmap = Bitmap.createBitmap(bitmap, saveLeft.toInt(), saveTop.toInt(), width.toInt(), height.toInt())

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PassportScans")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return AppResult.Error("Failed to create image Uri")

        contentResolver.openOutputStream(uri).use { outputStream ->
            if (outputStream == null) {
                return AppResult.Error("Failed to open output stream")
            }
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        contentResolver.update(uri, contentValues, null, null)

        AppResult.Success("Image saved successfully", uri)
    } catch (e: Exception) {
        Log.e("ImageCapture", "Error saving image", e)
        AppResult.Error("Image save failed: ${e.localizedMessage}")
    }
}

fun vibratePhone(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager.defaultVibrator
    } else{
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))

    try {
        MediaPlayer.create(context, R.raw.beep_sound)?.start() // Add beep_sound.mp3 in res/raw
    } catch (e: Exception) {
        Log.e("SoundFeedback", "Failed to play sound", e)
    }
}

fun validateEmail(input: String) : Pair<Boolean, String> {
    val isEmailError = !Patterns.EMAIL_ADDRESS.matcher(input).matches()
    val emailErrorMessage = if (isEmailError) "Invalid email address" else ""

    return isEmailError to emailErrorMessage
}

fun validatePhoneNumber(input: String) : Pair<Boolean, String> {
    val phonePattern = Regex("^\\+\\d{1,4}\\d{9,}$")
    val isPhoneNumberError = !phonePattern.matches(input)
    val phoneNumberErrorMessage = if (isPhoneNumberError) "Must include country code and digits only (e.g., +44712XXX-00)" else ""

    return Pair(isPhoneNumberError, phoneNumberErrorMessage)
}

fun isValidPassportNumber(input: String): Boolean {
    return Constants().passportRegex.matches(input)
}

fun isDriverLicenceValid(input: String): Boolean {
    return Constants().driverLicenceRegex.matches(input)
}

fun getOrCreateAES(alias: String = "my_key_alias"): SecretKey {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)

    // If Key Exist return it
    keyStore.getKey(alias, null)?.let {
        return it as SecretKey
    }

    // Create Key Otherwise
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        .setUserAuthenticationRequired(false)
        .build()

    keyGenerator.init(keyGenParameterSpec)
    return keyGenerator.generateKey()

}

fun encryptWithKeyStore(data: String, alias: String = "my_key_alias"): String {
    val secretKey = getOrCreateAES(alias)
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val iv = cipher.iv

    val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    val combinedData = iv + encryptedData
    val base64Iv = Base64.encodeToString(combinedData, Base64.NO_WRAP)
    return  base64Iv
}

fun decryptWithKeyStore(encryptedData: String, alias: String = "my_key_alias"): String {
    val secretKey = getOrCreateAES(alias)
    val decodedData = Base64.decode(encryptedData, Base64.NO_WRAP)
    val iv = decodedData.copyOfRange(0, 16)
    val encryptedBytes = decodedData.copyOfRange(16, decodedData.size)

    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
    val decryptedData = cipher.doFinal(encryptedBytes)
    return String(decryptedData, Charsets.UTF_8)

}

fun getOrCreateHMACKey(alias: String = "wallet_hmac_key"): SecretKey {
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)

    keyStore.getKey(alias, null)?.let {
        return it as SecretKey
    }

    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore")
    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    )
        .setUserAuthenticationRequired(false)
        .build()

    keyGenerator.init(keyGenParameterSpec)
    return keyGenerator.generateKey()
}

fun generateHMAC(message: String, secretKey: SecretKey) : String {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKey)
    val hmacBytes = mac.doFinal(message.toByteArray())
    return Base64.encodeToString(hmacBytes, Base64.URL_SAFE).trim()
}

fun verifyHMAC(message: String, hmac: String, secretKey: SecretKey): Boolean {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKey)
    val computedHmac = mac.doFinal(message.toByteArray())
    val providedHmac = Base64.decode(hmac, Base64.URL_SAFE)

    return computedHmac.contentEquals(providedHmac)
}

fun convertLongToDate(timeStamp: Long, pattern: String = "dd/MM/yyyy"): String {
    if(timeStamp == 0L) return ""

    val date = Date(timeStamp)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(date)
}

fun getUserLocation(activity: Activity, context: Context, onLocationDetected: (Location) -> Unit, onError: (String) -> Unit) {
    val permissionState = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

    if(permissionState != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        return
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token)
        .addOnSuccessListener { location ->
            if(location != null) {
                onLocationDetected(location)
            }
            else {
                onError("Location is unavailable")
                return@addOnSuccessListener
            }
        }
        .addOnFailureListener {
            onError("Failed to get location")
            return@addOnFailureListener
        }
}

suspend fun handleCaptureIdSuspend( imageCapture: ImageCapture, executor: Executor, analyser: IdAnalyser, context: Context): Uri? =
    suspendCancellableCoroutine { continuation ->

        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    try {
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

                        val savedImageUri = saveBitmapToFile(
                            bitmap,
                            context,
                            cropRect,
                            "id_${System.currentTimeMillis()}.jpg"
                        )

                        if(savedImageUri is AppResult.Error) {
                            Log.e("ImageCapture", "Error saving image: ${savedImageUri.message}")
                            if (continuation.isActive) continuation.resume(null) { cause, _, _ -> null?.let { it(cause) } }
                            return
                        }

                        val uri = savedImageUri.data

                        if (continuation.isActive) continuation.resume(uri) { cause, _, _ -> null?.let { it(cause) } }

                    } catch (e: Exception) {
                        Log.e("ImageCapture", "Capture processing failed", e)
                        if (continuation.isActive) continuation.resume(null) { cause, _, _ -> null?.let { it(cause) } }
                    } finally {
                        image.close()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("ImageCapture", "Error: ${exception.message}", exception)
                    if (continuation.isActive) continuation.resume(null) { cause, _, _ -> null?.let { it(cause) } }
                }
            }
        )
    }

fun isValidMnemonic(mnemonic: String): Boolean {
    val words = mnemonic.trim().split("\\s+".toRegex())

    // Mnemonics are typically 12, 15, 18, 21, or 24 words
    if (words.size !in listOf(12, 15, 18, 21, 24)) return false

    return try {
        MnemonicUtils.generateSeed(mnemonic, "") // Throws if invalid
        true
    } catch (e: Exception) {
        Log.e("isValidMnemonic", "Error validating mnemonic", e)
        false
    }
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)

    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

fun generateQRCode(content: String, size: Int = 512): Bitmap? {
    return try {
        val hints = hashMapOf<EncodeHintType, Any>(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] =
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun sendNotification(context: Context, content: String, text: String) {
    val builder = NotificationCompat.Builder(context, "election_channel")
        .setSmallIcon(R.drawable.ic_launcher) // Replace with your own icon
        .setContentTitle(content)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.notify(1001, builder.build())
}

fun isValidBlockchainAddress(address: String?): Boolean {
    if (address.isNullOrEmpty()) return false
    if (!address.startsWith("0x")) return false
    if (address.length != 42) return false

    val hexPart = address.substring(2)
    return hexPart.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
}

fun parseMillisToDate(millisStr: String?): LocalDate? {
    return try {
        val millis = millisStr?.toLongOrNull() ?: return null
        Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } catch (_: Exception) {
        null
    }
}

fun getElectionWithEarliestEndDate(elections: List<ElectionData>): ElectionData? {
    val today = LocalDate.now()

    return elections
        .filter {
            val endDate = parseMillisToDate(it.endTime.toString())
            println("Firestore $endDate")
            endDate != null && !endDate.isBefore(today)
        }
        .sortedWith(compareBy { parseMillisToDate(it.endTime.toString()) ?: LocalDate.MAX })
        .firstOrNull()
}

fun calculateTimeLeftMillis(endTime: Long): Long {
    val nowMillis = Instant.now().toEpochMilli()
    return ((endTime) - nowMillis).coerceAtLeast(0) / 1000
}

fun openWebsite(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = url.toUri()
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No browser found to open this link.", Toast.LENGTH_SHORT).show()
    }
}
