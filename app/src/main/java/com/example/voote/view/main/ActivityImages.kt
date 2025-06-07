package com.example.voote.view.main

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.voote.R
import com.example.voote.utils.Constants

@Composable
fun ActivityImages() {
    Image(
        painter = painterResource(R.drawable.ic_background),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Constants().cFullSizeModifier,
    )

    Image(
        painter = painterResource(R.drawable.ic_launcher),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Constants().cFullSizeModifier,
    )
}