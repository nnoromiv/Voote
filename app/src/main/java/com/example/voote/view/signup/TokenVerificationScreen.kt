package com.example.voote.view.signup

import android.app.Activity
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.voote.firebase.data.STATUS
import com.example.voote.navigation.RoutePersonalVerification
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.TokenVerificationViewModel
import kotlinx.coroutines.launch

@Composable
fun TokenVerificationScreen(phoneNumber: String, navController: NavController) {
    val auth = Auth()

    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    var isLoading by remember { mutableStateOf(false) }
    var isTokenResendLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val tokenVerificationViewModel: TokenVerificationViewModel = viewModel()

    val token by tokenVerificationViewModel.token.collectAsState()
    val storedVerificationId by tokenVerificationViewModel.storedVerificationId.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("TokenVerificationScreen", "Sending code to $phoneNumber")
        val result = auth.sendCode(activity, phoneNumber)

        if(result.isFailure) {
            Log.e("TokenVerificationScreen", "Error sending code to $phoneNumber", result.exceptionOrNull())
            Toast.makeText(activity, result.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
            isLoading = false
            return@LaunchedEffect
        }

        Toast.makeText(activity, "Code sent to $phoneNumber", Toast.LENGTH_SHORT).show()
        tokenVerificationViewModel.setStoredVerificationId(result.getOrThrow().data.toString())
        isLoading = false

    }

    fun resendToken() {
        isTokenResendLoading = true

        coroutineScope.launch {
            val result = auth.sendCode(activity, phoneNumber)

            if(result.isFailure) {
                Toast.makeText(context, result.exceptionOrNull()?.message, Toast.LENGTH_SHORT).show()
                isTokenResendLoading = false
                return@launch
            }

            Toast.makeText(context, "Token resent", Toast.LENGTH_SHORT).show()
            tokenVerificationViewModel.setStoredVerificationId(result.getOrThrow().data.toString())
            isTokenResendLoading = false
        }
    }

    fun verifyToken() {

        isLoading = true

        if (storedVerificationId.isEmpty()) {
            Toast.makeText( context, "Can't verify token, Retry later", Toast.LENGTH_SHORT ).show()
            isLoading = false
            return
        }

        if (token.any { it.isEmpty() }) {
            Toast.makeText(context, "Invalid token", Toast.LENGTH_SHORT).show()
            isLoading = false
            return
        }

        coroutineScope.launch {
            val result = auth.verifyPhoneNumber(storedVerificationId, token.joinToString(""))

            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            isLoading = false

            if(result.status == STATUS.ERROR) {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                return@launch
            }

            navController.navigate(RoutePersonalVerification)

        }
    }

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
                text = "Token sent to you",
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

            if(isTokenResendLoading) {
                Loader()
            } else {
                CTextButton(
                    text = "Resend Token",
                    onClick = { resendToken() },
                    modifier = Modifier.align(Alignment.End),
                    color = Color(0x401B1B1B),
                )
            }

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
                        onClick = { verifyToken() },
                        modifier = Modifier.padding(vertical = 20.dp),
                        enabled = token.all { it.isNotEmpty() }
                    )
                }
            }
        }
    }
}
