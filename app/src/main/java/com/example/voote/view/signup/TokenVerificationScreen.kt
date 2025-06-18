package com.example.voote.view.signup

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Auth
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.TokenVerificationViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun TokenVerificationScreen(navController: NavController) {
    var auth: FirebaseAuth = Firebase.auth

    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    var isLoading by remember { mutableStateOf(false) }

    val tokenVerificationViewModel: TokenVerificationViewModel = viewModel()
    val token by tokenVerificationViewModel.token.collectAsState()
    val storedPhoneNumber by tokenVerificationViewModel.storedPhoneNumber.collectAsState()
    val storedVerificationId by tokenVerificationViewModel.storedVerificationId.collectAsState()
    val resendToken by tokenVerificationViewModel.resendToken.collectAsState()

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Text(
                text = "Verify Your Account",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Token sent to your number",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                TokenField(
                    tokens = token,
                    onTokenChange = { index, value ->
                        tokenVerificationViewModel.setToken(index, value)
                    }
                )
            }

            CTextButton(
                text = "Resend Token",
                onClick = {
                    if(resendToken == null || storedPhoneNumber.isEmpty()) {
                        Toast.makeText(context, "Resending token failed", Toast.LENGTH_SHORT).show()
                        return@CTextButton
                    }

                    Auth().sendVerificationCode(auth, activity, storedPhoneNumber.toString(), navController, true)
                },
                modifier = Modifier.align(Alignment.End),
                color = Color(0x401B1B1B),
                enabled = true
            )

            if(isLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Loader()
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {

                    PrimaryButton(
                        text = "Continue",
                        onClick = {

                            isLoading = true

                            if (storedVerificationId.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Verification failed, resending token",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isLoading = false
                                return@PrimaryButton
                            }

                            if (token.any { it.isEmpty() }) {
                                Toast.makeText(context, "Invalid token", Toast.LENGTH_SHORT).show()
                                isLoading = false
                                return@PrimaryButton
                            }

                            try {

                                Auth().verifyPhoneNumberWithCode(
                                    auth,
                                    storedVerificationId,
                                    token.joinToString(""),
                                    navController
                                )
                                isLoading = false
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            }
        }
    }
}
