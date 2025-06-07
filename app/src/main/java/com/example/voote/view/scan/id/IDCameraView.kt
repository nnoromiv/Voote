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
import androidx.navigation.NavController
import com.example.voote.navigation.Address
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.utils.IdAnalyser

@Composable
fun IDCameraView(
    navController: NavController,
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
                    .align(Alignment.Center)
                    .width(300.dp)
                    .height(200.dp)
                    .border(3.dp, borderColor, RoundedCornerShape(8.dp))
                    .background(Color.Transparent)
            )

            // Optional: Label or instruction
            Component().Text(
                "Align your passport within the box",
                color = Color.White,
                fontSize = 16,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            )

            // Debug Button: Remove later
            PrimaryButton(
                text = "Skip",
                onClick = {
                    navController.navigate(Address)
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp)
            )

            IconButton(
                onClick = onClick,
                enabled = cameraReady.value,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Camera,
                    contentDescription = "Capture",
                    tint = if (cameraReady.value) Color.White else Color.Gray,
                    modifier = Modifier.size(100.dp).padding(bottom = 40.dp)
                )
            }

        }
    }
}