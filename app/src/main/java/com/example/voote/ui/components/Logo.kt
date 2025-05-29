package com.example.voote.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.voote.R

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter =  painterResource(R.drawable.ic_launcher_trimmed),
        contentDescription = null,
        modifier = modifier.width(150.dp).height(50.dp)
    )
}