package com.example.voote.view.signup

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.voote.navigation.Login
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.SignUpViewModel

@Composable
fun SignUpButton(activity: Activity, navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val auth = authViewModel.auth

    val signUpViewModel : SignUpViewModel = viewModel()
    val email by signUpViewModel.email.collectAsState()
    val firstName by signUpViewModel.firstName.collectAsState()
    val lastName by signUpViewModel.lastName.collectAsState()
    val phoneNumber by signUpViewModel.phoneNumber.collectAsState()
    val password by signUpViewModel.password.collectAsState()
    val isEmailError by signUpViewModel.isEmailError.collectAsState()
    val emailErrorMessage by signUpViewModel.emailErrorMessage.collectAsState()
    val isPhoneNumberError by signUpViewModel.isPhoneNumberError.collectAsState()
    val phoneNumberErrorMessage by signUpViewModel.phoneNumberErrorMessage.collectAsState()

    val errorExist = firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || isEmailError || isPhoneNumberError || emailErrorMessage.isNotEmpty() || phoneNumberErrorMessage.isNotEmpty()
    var isLoading by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AgreementText()

        if(isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                Loader()
            }
        } else {
            PrimaryButton(
                text = stringResource(R.string.sign_up),
                onClick = {
                    isLoading = true

                    if(password.length < 6) {
                        Toast.makeText(activity, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@PrimaryButton
                    }

                    try {

                        Auth().handleSignUp(
                            activity,
                            auth,
                            email,
                            firstName,
                            lastName,
                            phoneNumber,
                            password,
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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
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

@Composable
fun AgreementText() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(0.dp)
        ) {
            Text(
                text = "By signing up, you agree to our",
                softWrap = true,
                fontSize = 16,
                fontWeight = FontWeight.Normal
            )
            CTextButton(text = "Terms,", onClick = {}, fonSize = 16)

        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(0.dp)
        ) {
            Text(
            text = "Privacy Policy",
            softWrap = true,
            fontSize = 16,
            fontWeight = FontWeight.Bold
            )
            Text(
                text = " and ",
                fontSize = 16,
                fontWeight = FontWeight.Normal
            )
            CTextButton(text = "Cookies.", onClick = {}, fonSize = 16)
        }
    }
}

