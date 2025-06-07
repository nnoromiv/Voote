package com.example.voote.view.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo

@Composable
fun LoginScreen(navController: NavController) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold {
        innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Component().Text(
                text = "Login to Voote",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Component().Text(
                text = "Log in to continue",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LoginForm(
                    phone = phone,
                    onPhoneChange = { phone = it },
                    password = password,
                    onPasswordChange = { password = it }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                LoginButtons(
                    phone = phone,
                    password = password,
                    navController = navController
                )
            }
        }
    }
}
