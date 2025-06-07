package com.example.voote.view.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Component

@Composable
fun LoginForm(
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
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

        CTextButton(
            text = "Forgot Password?",
            onClick = {},
            modifier = Modifier.align(Alignment.End),
            color = Color(0x401B1B1B)
        )

        Icon(
            imageVector = Icons.Outlined.Fingerprint,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 20.dp).size(70.dp)
        )
    }
}