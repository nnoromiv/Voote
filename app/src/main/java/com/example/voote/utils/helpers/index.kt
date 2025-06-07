package com.example.voote.utils.helpers

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.geometry.Rect
import com.example.voote.R
import java.io.IOException

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

