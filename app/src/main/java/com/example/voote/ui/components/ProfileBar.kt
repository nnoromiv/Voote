package com.example.voote.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProfileBar(
    name: String = "John Doe",
    username: String = "jonny",
    userImageUri: Uri?,
    imageVector: ImageVector? = Icons.Outlined.Notifications
) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(
                    userImageUri = userImageUri
                )

                ProfileInfo(
                    name,
                    username
                )
            }
        }

        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = imageVector!!,
                contentDescription = "Profile Bar Icon"
            )
        }
    }
}

@Composable
fun ProfileImage(
    userImageUri: Uri?
) {
    AsyncImage(
        model = userImageUri,
        contentDescription = "User Image",
        modifier = Modifier.size(80.dp).clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ProfileInfo(
    name: String,
    username: String
) {
    Column {
        Component().Text(
            text = name,
            fontSize = 20,
            fontWeight = FontWeight.Bold,
        )

        Component().Text(
            text = "@$username",
            fontSize = 16,
            fontWeight = FontWeight.Normal,
            color = Color(0x601B1B1B)
        )
    }
}