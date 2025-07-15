package com.example.voote.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfileBar( name: String, userName: String, imageVector: ImageVector? = Icons.Outlined.Notifications, onClick: () -> Unit = {}) {

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
                ProfileWithInitials(name)

                ProfileInfo(
                    name,
                    userName
                )
            }
        }

        IconButton(
            onClick = { onClick() }
        ) {
            Icon(
                imageVector = imageVector!!,
                contentDescription = "Profile Bar Icon"
            )
        }
    }
}

//@Composable
//fun ProfileImage(
//    userImageUri: Uri?
//) {
//    AsyncImage(
//        model = userImageUri,
//        contentDescription = "User Image",
//        modifier = Modifier.size(48.dp).clip(CircleShape),
//        contentScale = ContentScale.Crop
//    )
//}

@Composable
fun ProfileWithInitials(name: String) {
    val initials = name
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .take(2)
        .joinToString("")


    AvatarView(
        initials = initials
    )
}

@Composable
fun AvatarView(initials: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}



@Composable
fun ProfileInfo(name: String, userName: String) {
    Column {
        Text(
            text = name,
            fontSize = 20,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "@$userName",
            fontSize = 16,
            fontWeight = FontWeight.Normal,
            color = Color(0x601B1B1B)
        )
    }
}