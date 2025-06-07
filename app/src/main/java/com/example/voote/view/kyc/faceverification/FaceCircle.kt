package com.example.voote.view.kyc.faceverification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.voote.utils.Constants

@Composable
fun FaceCircle(
    onClick: () -> Unit,
    userImageUri: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(350.dp)
                .clip(CircleShape)
                .background(Color(0x401B1B1B)) // dark circular background
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (userImageUri.isBlank())
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Capture Face",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                ) else
                AsyncImage(
                    model = userImageUri.toUri(),
                    contentDescription = "Captured Face",
                    modifier = Modifier.size(350.dp),
                    contentScale = ContentScale.Crop
                )

        }

        Text(
            text = "Tap to capture a picture of your face",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontFamily = Constants().centuryFontFamily
        )
    }
}