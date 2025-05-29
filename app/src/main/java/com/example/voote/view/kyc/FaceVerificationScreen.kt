package com.example.voote.view.kyc

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.voote.navigation.Routes
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.utils.Constants

@Composable
fun FaceVerificationScreen(navController: NavController) {
    var userImageUri by remember { mutableStateOf<Uri?>(null) }

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Component().Text(
                text = "Verify Address",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Component().Text(
                text = "Provide your current residential address",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
                softWrap = true,
            )

            HorizontalDivider(
                color = Color(0x401B1B1B),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CaptureFaceButton(
                    onClick = {
                        userImageUri = userImageUri?.let { null } ?: Constants().imageUrl.toUri()
                    },
                    userImageUri = userImageUri
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PrimaryButton(
                        text = "Continue",
                        onClick = {
                            navController.navigate(Routes.HOME)
                        },
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    COutlinedButton(
                        text = "Retake Photo",
                        onClick = {
                            userImageUri = null
                        },
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                }
            }
        }
    }
}

@Composable
fun CaptureFaceButton(
    onClick: () -> Unit,
    userImageUri: Uri?
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
            if (userImageUri == null)
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Capture Face",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            ) else
            AsyncImage(
                model = userImageUri,
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
