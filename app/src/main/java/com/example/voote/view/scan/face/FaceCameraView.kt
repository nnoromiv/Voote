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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.voote.ui.components.Text
import com.example.voote.utils.FaceAnalyser

@Composable
fun FaceCameraView(previewView: PreviewView,  analyser: FaceAnalyser) {

    val borderColor by animateColorAsState(
        targetValue = if (analyser.isFaceInBox.value) Color.Green else Color.Red,
        animationSpec = tween(500)
    )

    val ovalShape: Shape = GenericShape { size, _ ->
        addOval(androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height))
    }

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
                    .height(300.dp)
                    .border(3.dp, borderColor, ovalShape)
                    .background(Color.Transparent, shape = ovalShape)
            )

            Text(
                "Look Straight into the Camera",
                color = Color.White,
                fontSize = 16,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            )

        }
    }
}