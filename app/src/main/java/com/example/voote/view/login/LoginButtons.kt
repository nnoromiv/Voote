package com.example.voote.view.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.voote.R
import com.example.voote.firebase.auth.Auth
import com.example.voote.navigation.Signup
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.LoginViewModel

@Composable
fun LoginButtons(navController: NavController) {

    val loginViewModel: LoginViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val auth = authViewModel.auth
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isEmailError by loginViewModel.isEmailError.collectAsState()
    val emailErrorMessage by loginViewModel.emailErrorMessage.collectAsState()

    val errorExist = password.isEmpty() || email.isEmpty() || isEmailError || emailErrorMessage.isNotEmpty()
    var isLoading by remember { mutableStateOf(false) }

    val activity = LocalActivity.current as Activity

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                Loader()
            }
        } else {
            PrimaryButton(
                text = stringResource(R.string.login),
                onClick = {
                    isLoading = true

                    try {

                        Auth().handleLogIn(
                            auth,
                            email,
                            password,
                            onError = { errorMessage ->
                                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                                isLoading = false
                            },
                            navController
                        )
                        isLoading = false
                    } catch (e: Exception) {
                        isLoading = false
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    }

                },
                enabled = !errorExist,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
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