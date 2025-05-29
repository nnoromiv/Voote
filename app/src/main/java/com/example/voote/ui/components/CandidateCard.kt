package com.example.voote.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CandidateCard(
    candidateImageUri: Uri?,
    candidateName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Black
        ),
        onClick = onClick,
        ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = candidateImageUri,
                contentDescription = "User Image",
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Component().Text(
                    text = if (candidateName.length > 10) candidateName.substring(0, 8) + ".." else candidateName,
                    fontSize = 12,
                    softWrap = true,
                )
            }
        }
    }
}