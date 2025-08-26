package com.example.voote.view.kyc

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.RouteHome
import com.example.voote.navigation.RouteLogin
import com.example.voote.navigation.RoutePassportVerification
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun PersonalVerificationButton(authViewModel: AuthViewModel, navController: NavController) {

    val isLoggedIn by remember { derivedStateOf{ authViewModel.isLoggedIn() } }
    val coroutineScope = rememberCoroutineScope()

    fun handleSkip() {
        Log.d("PersonalVerificationButton", "handleSkip called $isLoggedIn")
        coroutineScope.launch {
            if (!isLoggedIn) {
                navController.navigate(RouteLogin)
                return@launch
            }

            navController.navigate(RouteHome)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Begin Verification",
            onClick = {
                navController.navigate(RoutePassportVerification)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )

        COutlinedButton(
            text = "Skip",
            onClick = { handleSkip() },
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "We have a ",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Privacy Policy",
                onClick = {},
            )
        }
    }
}
