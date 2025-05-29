package com.example.voote.view.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Face6
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.R
import com.example.voote.navigation.Routes
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton

@Composable
fun SignUpScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Component().Text(
                text = "Welcome to",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )
            Component().Text(
                text = stringResource(R.string.app_name),
                fontSize = 30,
                fontWeight = FontWeight.Bold,
            )
            Component().Text(
                text = "Create an account to continue",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )

            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {
                Top(
                    firstName = firstName,
                    onFirstNameChange = { firstName = it },
                    lastName = lastName,
                    onLastNameChange = { lastName = it },
                    email = email,
                    onEmailChange = { email = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    password = password,
                    onPasswordChange = { password = it }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                Bottom(
                    phone = phone,
                    password = password,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun Top(
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

@Composable
fun Bottom(
    phone: String,
    password: String,
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Component().Text(
                text = "By signing up, you agree to our ",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
                softWrap = true
            )
            CTextButton(
                text = "Terms, ",
                onClick = {},
                fonSize = 16
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CTextButton(
                text = "Privacy Policy ",
                onClick = {},
                fonSize = 16
            )
            Component().Text(
                text = "and ",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Cookies Policy.",
                onClick = {},
                fonSize = 16
            )
        }

        PrimaryButton(
            text = stringResource(R.string.sign_up),
            onClick = {
                println("Phone: $phone, Password: $password")
                navController.navigate(Routes.TOKEN_VERIFICATION)
            },
            modifier = Modifier.padding(vertical = 20.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Component().Text(
                text = "Already a user?",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Log In",
                onClick = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }
    }
}