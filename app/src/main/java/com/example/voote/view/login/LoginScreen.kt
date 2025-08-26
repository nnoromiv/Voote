package com.example.voote.view.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.RouteHome
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel

@Composable
fun LoginScreen(authManager: AuthViewModel, userViewModel: UserViewModel, kycViewModel: KycViewModel, navController: NavController) {

    val isLoggedIn by remember { derivedStateOf{ authManager.isLoggedIn() } }

    LaunchedEffect(isLoggedIn) {
        if(isLoggedIn) navController.navigate(RouteHome)
    }

    Scaffold {
        innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Text(
                text = "Login to Voote",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Log in to continue",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LoginForm()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                LoginButtons(userViewModel, kycViewModel, navController)
            }
        }
    }
}
