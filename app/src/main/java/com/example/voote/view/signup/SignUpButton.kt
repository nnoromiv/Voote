package com.example.voote.view.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.R
import com.example.voote.navigation.Login
import com.example.voote.navigation.TokenVerification
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.PrimaryButton

@Composable
fun SignUpButton(
    phone: String,
    password: String,
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Component().Text(
                text = "By signing up, you agree to our ",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
                softWrap = true
            )
            CTextButton(
                text = "Terms, ",
                onClick = {},
                fonSize = 16
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CTextButton(
                text = "Privacy Policy ",
                onClick = {},
                fonSize = 16
            )
            Component().Text(
                text = "and ",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Cookies Policy.",
                onClick = {},
                fonSize = 16
            )
        }

        PrimaryButton(
            text = stringResource(R.string.sign_up),
            onClick = {
                println("Phone: $phone, Password: $password")
                navController.navigate(TokenVerification)
            },
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Component().Text(
                text = "Already a user?",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Log In",
                onClick = {
                    navController.navigate(Login)
                }
            )
        }
    }
}