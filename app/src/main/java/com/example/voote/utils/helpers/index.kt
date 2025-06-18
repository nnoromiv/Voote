package com.example.voote.utils.helpers

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.ui.geometry.Rect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voote.R
import com.example.voote.utils.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.IOException
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

fun saveBitmapToFile(bitmap: Bitmap, context: Context, cropRect: Rect, fileName: String): Uri? {
    var imageUri: Uri? = null
    val croppedBitmap : Bitmap

    try {
        // Ensure crop rectangle is within the bounds of the original bitmap
        val saveLeft = cropRect.left.coerceIn(0f, bitmap.width.toFloat())
        val saveTop = cropRect.top.coerceIn(0f, bitmap.height.toFloat())
        val saveRight = cropRect.right.coerceIn(saveLeft, bitmap.width.toFloat())
        val saveBottom = cropRect.bottom.coerceIn(saveTop, bitmap.height.toFloat())

        val width = (saveRight - saveLeft).coerceAtLeast(1F)
        val height = (saveBottom - saveTop).coerceAtLeast(1F)

        croppedBitmap = Bitmap.createBitmap(bitmap, saveLeft.toInt(), saveTop.toInt(), width.toInt(), height.toInt())

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PassportScans")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = context.contentResolver
        val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            contentResolver.openOutputStream(it).use { outputStream ->
                if (outputStream != null) {
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(it, contentValues, null, null)
            imageUri = it
        }

    } catch (e: IOException) {
        Log.e("ImageCapture", "Error saving image", e)
    } catch (e: IllegalArgumentException) {
        Log.e("ImageCapture", "Invalid crop rect", e)
    }

    return imageUri
}

fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        Log.e("PassportAnalyser", "Failed to load bitmap from URI", e)
        null
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
    var isEmailError = !Patterns.EMAIL_ADDRESS.matcher(input).matches()
    var emailErrorMessage = if (isEmailError) "Invalid email address" else ""

    return isEmailError to emailErrorMessage
}

fun validatePhoneNumber(input: String) : Pair<Boolean, String> {
    val phonePattern = Regex("^\\+\\d{1,4}\\d{9,}$")
    var isPhoneNumberError = !phonePattern.matches(input)
    var phoneNumberErrorMessage = if (isPhoneNumberError) "Phone number must include country code and digits only (e.g., +44712XXX-00)" else ""

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
    val base64Iv = Base64.encodeToString(combinedData, Base64.DEFAULT)
    return  base64Iv
}

fun decryptWithKeyStore(encryptedData: String, alias: String = "my_key_alias"): String {
    val secretKey = getOrCreateAES(alias)
    val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
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
    return Base64.encodeToString(hmacBytes, Base64.NO_WRAP)
}

fun verifyHMAC(message: String, hmac: String, secretKey: SecretKey): Boolean {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKey)
    val computedHmac = mac.doFinal(message.toByteArray())
    val providedHmac = Base64.decode(hmac, Base64.NO_WRAP)

    return computedHmac.contentEquals(providedHmac)
}

fun convertLongToDate(timeStamp: Long, pattern: String = "dd/MM/yyyy"): String {
    if(timeStamp == 0L) return ""

    val date = Date(timeStamp)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(date)
}

fun getUserLocation(activity: Activity, context: Context, onLocationDetected: (Location) -> Unit, onError: (String) -> Unit) {
    var permissionState = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

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

