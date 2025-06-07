package com.example.voote.view.signup

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
import com.example.voote.navigation.PersonalVerification
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton

@Composable
fun TokenVerificationScreen(navController: NavController) {
    var emailToken by remember { mutableStateOf(List(6) {""})}
    var phoneToken by remember { mutableStateOf(List(6) {""})}

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Component().Text(
                text = "Verify Your Account",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Component().Text(
                text = "Token sent to your mail",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                TokenField(
                    tokens = emailToken,
                    onTokenChange = { index, value ->
                        emailToken = emailToken.mapIndexed { i, t ->
                            if (i == index) value else t
                        }
                    }
                )
            }

            Component().Text(
                text = "Token sent to your number",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                TokenField(
                    tokens = phoneToken,
                    onTokenChange = { index, value ->
                        phoneToken = phoneToken.mapIndexed { i, t ->
                            if (i == index) value else t
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                PrimaryButton(
                    text = "Continue",
                    onClick = {
                        println("Tokens: ${emailToken.joinToString("")}, ${phoneToken.joinToString("")}")
                        navController.navigate(PersonalVerification)
                    },
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    }
}
