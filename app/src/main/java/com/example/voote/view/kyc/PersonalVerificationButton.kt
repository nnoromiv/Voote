package com.example.voote.view.kyc

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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

@Composable
fun PersonalVerificationButton(context: Context, authViewModel: AuthViewModel,  navController: NavController) {

    val isLoggedIn = authViewModel.isLoggedIn()

    fun handleClick(isSkipped: Boolean) {
        if (!isLoggedIn) {
            Toast.makeText(context, "Log in to continue", Toast.LENGTH_SHORT).show()
            navController.navigate(RouteLogin)
            return
        }

        if (isSkipped) {
            navController.navigate(RouteHome)
        } else {
            navController.navigate(RoutePassportVerification)
        }

    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Begin Verification",
            onClick = { handleClick(false) },
            modifier = Modifier.padding(vertical = 10.dp)
        )

        COutlinedButton(
            text = "Skip",
            onClick = { handleClick(true) },
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
