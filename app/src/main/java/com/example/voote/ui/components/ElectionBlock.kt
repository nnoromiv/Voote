package com.example.voote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.model.data.candidateItem
import com.example.voote.navigation.DynamicElectionScreen

@Composable
fun ElectionBlock(navController : NavController) {
    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Component().Text(
                text = "Presidential Election 2025",
                fontSize = 16,
                fontWeight = FontWeight.Bold,
            )

            IconButton(
                onClick = {
                    navController.navigate(DynamicElectionScreen)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = "See All"
                )
            }
        }

        ElectionCandidateColumn(items = candidateItem)
    }
}