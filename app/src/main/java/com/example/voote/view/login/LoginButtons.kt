package com.example.voote.view.login

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
import com.example.voote.navigation.Signup
import com.example.voote.navigation.homeObject
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.PrimaryButton

@Composable
fun LoginButtons(
    phone: String,
    password: String,
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = stringResource(R.string.login),
            onClick = {
                println("Phone: $phone, Password: $password")
                navController.navigate(homeObject)
            },
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Component().Text(
                text = "New Here?",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Sign Up",
                onClick = {
                    navController.navigate(Signup)
                }
            )
        }
    }
}