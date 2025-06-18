package com.example.voote.view.scan.face

import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.voote.navigation.AddressVerification
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.utils.FaceAnalyser


@Composable
fun FaceCameraView(
    navController: NavController,
    previewView: PreviewView,
    analyser: FaceAnalyser,
) {

    val borderColor by animateColorAsState(
        targetValue = if (analyser.isFaceInBox.value) Color.Green else Color.Red,
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
                    .width(250.dp)
                    .height(300.dp)
                    .clip(CircleShape)
                    .border(3.dp, borderColor, CircleShape)
                    .background(Color.Transparent)
            )

            // Optional: Label or instruction
            Text(
                "Align your face within the circle",
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
                    navController.navigate(AddressVerification)
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp)
            )

        }
    }
}