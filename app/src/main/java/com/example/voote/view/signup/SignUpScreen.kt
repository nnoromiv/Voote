package com.example.voote.view.signup

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel


@Composable
fun SignUpScreen(userViewModel: UserViewModel, kycViewModel: KycViewModel, navController: NavController) {

    val activity = LocalActivity.current as Activity

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
                text = "Create an Account",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Create an account to continue",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SignUpForm()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                SignUpButton(activity, userViewModel, kycViewModel, navController)
            }
        }
    }
}
