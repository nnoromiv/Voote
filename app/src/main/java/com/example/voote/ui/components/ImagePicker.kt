package com.example.voote.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.composables.icons.lucide.ImagePlus
import com.composables.icons.lucide.Lucide

@Composable
fun ImagePicker(
    label: String = "Select Image",
    onImageSelected: (Uri?) -> Unit
) {

    // Launcher to pick image from gallery
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    PrimaryButton(
        text = label,
        onClick = { launcher.launch("image/*") },
        icon = {
            Icon(
                imageVector = Lucide.ImagePlus,
                contentDescription = "Select Image"
            )
        }
    )
}
