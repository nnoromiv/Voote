package com.example.voote.view.kyc.faceverification

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.navigation.ScanFace
import com.example.voote.navigation.homeObject
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.utils.helpers.generateHMAC
import com.example.voote.utils.helpers.getOrCreateHMACKey
import com.example.voote.utils.helpers.verifyHMAC
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.WalletViewModel

@Composable
fun FaceVerificationScreen(navController: NavController, userImageUri: String) {
    val context = LocalContext.current

    val secretKey = getOrCreateHMACKey()

    val authViewModel : AuthViewModel = viewModel()
    val walletViewModel : WalletViewModel = viewModel()

    val uid = authViewModel.userUid().toString()

    val address = walletViewModel.address
    val walletId = generateHMAC(uid + address, secretKey)

    val isValid = verifyHMAC(uid + address, walletId, secretKey)

    fun handleFaceImage() {
        if(userImageUri.isEmpty()) {
            Toast.makeText(context, "Please capture your face", Toast.LENGTH_SHORT).show()
            return
        }

        val imageUri = userImageUri.toUri()

        if(!isValid) {
            Toast.makeText(context, "Invalid User", Toast.LENGTH_SHORT).show()
            return
        }

        val imageToSave = "faceImage"
        Verification().uploadImage(imageUri, imageToSave, imageToSave) {
                onError -> Log.e("ImageCapture", "Error uploading image: ${onError.message}")
        }

        Toast.makeText(context, "Image saved", Toast.LENGTH_LONG).show()

        navController.navigate(homeObject)
    }

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
                        navController.navigate(ScanFace)
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
