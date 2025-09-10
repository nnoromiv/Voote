package com.example.voote.view.kyc.faceverification

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.firebase.data.STATUS
import com.example.voote.navigation.RouteLogin
import com.example.voote.navigation.RouteScanFace
import com.example.voote.navigation.RouteStatus
import com.example.voote.navigation.toJson
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.view.LoaderScreen
import com.example.voote.viewModel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun FaceVerificationScreen(authManager: AuthViewModel, userImageUri: String, navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val verification = Verification(authManager)

    val isLoading = remember { mutableStateOf(false) }

    fun handleFaceImage() {
        if(userImageUri.isEmpty()) {
            Toast.makeText(context, "Please capture your face", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUri = userImageUri.toUri()

        val fileName = "faceImage"

        isLoading.value = true

        coroutineScope.launch {
            val result = verification.uploadImage(imageUri, fileName)

            if(result.status == STATUS.ERROR) {

                navController.navigate(RouteStatus(
                    status = result.status,
                    nextScreen = ""
                ))

                return@launch
            }

            navController.navigate(RouteStatus(
                status = result.status,
                nextScreen = RouteLogin.toJson()
            ))

        }

    }

    if(isLoading.value) {
        LoaderScreen()
    } else {
        Scaffold {
                innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Logo()

                Text(
                    text = "Face Recognition",
                    fontSize = 30,
                    fontWeight = FontWeight.Bold
                )

                Text(
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
                            navController.navigate(RouteScanFace)
                        },
                        userImageUri
                    )
                }

                PrimaryButton(
                    text = "Continue",
                    onClick = { handleFaceImage() },
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}
