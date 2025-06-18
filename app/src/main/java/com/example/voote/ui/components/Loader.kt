package com.example.voote.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Loader() {
    CircularProgressIndicator(
        modifier = Modifier.padding(vertical = 20.dp),
        color = Color(0xFF1B1B1B),
        strokeWidth = 2.dp
    )
}