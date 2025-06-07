package com.example.voote.view.kyc.faceverification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.ScanFace
import com.example.voote.navigation.homeObject
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton

@Composable
fun FaceVerificationScreen(navController: NavController, userImageUri: String) {

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()

            Component().Text(
                text = "Face Recognition",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Component().Text(
                text = "Capture your face",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
                softWrap = true,
            )

            HorizontalDivider(
                color = Color(0x401B1B1B),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                FaceCircle(
                    onClick = {
                        navController.navigate(ScanFace)
                    },
                    userImageUri
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PrimaryButton(
                        text = "Continue",
                        onClick = {
                            navController.navigate(homeObject)
                        },
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    COutlinedButton(
                        text = "Retake Photo",
                        onClick = {},
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                }
            }
        }
    }
}
