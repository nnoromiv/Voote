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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.Component

@Composable
fun SignUpForm(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
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
                Component().TextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
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
                Component().TextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
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

        Component().TextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
            )
        )

        Component().TextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Phone Number") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
            )
        )

        Component().TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Password,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
            )
        )
    }
}