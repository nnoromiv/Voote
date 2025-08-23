package com.example.voote.view.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voote.ui.components.ErrorHandlingInputField
import com.example.voote.ui.components.TextField
import com.example.voote.utils.helpers.validateEmail
import com.example.voote.viewModel.LoginViewModel

@Composable
fun LoginForm() {
    val loginViewModel: LoginViewModel = viewModel()
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isEmailError by loginViewModel.isEmailError.collectAsState()
    val emailErrorMessage by loginViewModel.emailErrorMessage.collectAsState()

    fun onEmailChange(value: String) {
        val (error, message) = validateEmail(value.trim())
        loginViewModel.setIsEmailError(error)
        loginViewModel.setEmailErrorMessage(message)
        loginViewModel.setEmail(value.trim())
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {

        ErrorHandlingInputField(
            email,
            onValueChange = { onEmailChange(it) },
            isError = isEmailError,
            errorMessage = emailErrorMessage,
            label = "Email",
            imageVector = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
            )
        )

        TextField(
            value = password,
            onValueChange = { loginViewModel.setPassword(it.trim()) },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
            ),
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Password,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
        )
    }
}