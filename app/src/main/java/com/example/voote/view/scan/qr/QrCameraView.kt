package com.example.voote.view.scan.qr

import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.voote.ui.components.Component

@Composable
fun QrCameraView (
    previewView: PreviewView
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Camera Preview
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(200.dp)
                    .height(200.dp)
                    .border(3.dp, Color.White, RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
            )

            // Optional: Label or instruction
            Component().Text(
                "Align code within the box",
                color = Color.White,
                fontSize = 16,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            )
        }
    }
}