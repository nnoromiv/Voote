package com.example.voote.view.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Face6
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Phone
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
import com.example.voote.utils.helpers.validatePhoneNumber
import com.example.voote.viewModel.SignUpViewModel

@Composable
fun SignUpForm() {

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

    fun onEmailChange(value: String) {
        val (error, message) = validateEmail(value.trim())
        signUpViewModel.setIsEmailError(error)
        signUpViewModel.setEmailErrorMessage(message)
        signUpViewModel.setEmail(value.trim())
    }

    fun onPhoneNumberChange(value: String) {
        val (error, message) = validatePhoneNumber(value.trim())
        signUpViewModel.setIsPhoneNumberError(error)
        signUpViewModel.setPhoneNumberErrorMessage(message)
        signUpViewModel.setPhoneNumber(value.trim())
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                TextField(
                    value = firstName,
                    onValueChange = { signUpViewModel.setFirstName(it.trim()) },
                    label = { Text("First Name") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Face6,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                TextField(
                    value = lastName,
                    onValueChange = { signUpViewModel.setLastName(it.trim()) },
                    label = { Text("Last Name") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Face,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                )
            }


        }

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

        ErrorHandlingInputField(
            phoneNumber,
            onValueChange = { onPhoneNumberChange(it) },
            isError = isPhoneNumberError,
            errorMessage = phoneNumberErrorMessage,
            label = "Phone Number",
            imageVector = Icons.Outlined.Phone,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
            )
        )


        TextField(
            value = password,
            onValueChange = { signUpViewModel.setPassword(it.trim()) },
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
