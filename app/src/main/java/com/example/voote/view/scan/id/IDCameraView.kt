package com.example.voote.view.scan.id

import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.voote.ui.components.Text
import com.example.voote.utils.IdAnalyser

@Composable
fun IDCameraView(
    previewView: PreviewView,
    onClick : () -> Unit,
    analyser: IdAnalyser,
    cameraReady: MutableState<Boolean>
) {

    val borderColor by animateColorAsState(
        targetValue = if (analyser.isIDinBox.value) Color.Green else Color.Red,
        animationSpec = tween(500)
    )

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

            // Overlay Box (passport should be within this)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(250.dp)
                    .height(200.dp)
                    .padding(top = 20.dp)
                    .border(3.dp, borderColor, RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
            )

            // Optional: Label or instruction
            Text(
                "Align your document within the box",
                color = Color.White,
                fontSize = 16,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = 20.dp, bottom = 20.dp)
            )

            IconButton(
                onClick = { onClick() },
                enabled = cameraReady.value,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Camera,
                    contentDescription = "Capture",
                    tint = if (cameraReady.value) Color.White else Color.Gray,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 40.dp)
                )
            }

        }
    }
}