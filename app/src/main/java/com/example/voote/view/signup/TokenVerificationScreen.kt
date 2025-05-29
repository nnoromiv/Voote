package com.example.voote.view.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.voote.navigation.Routes
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.utils.Constants

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
                        navController.navigate(Routes.PERSONAL_VERIFICATION)
                    },
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    }
}

@Composable
fun TokenField(
    tokens: List<String>,
    onTokenChange: (Int, String) -> Unit
) {
    val focusRequester = List(6) { FocusRequester() }

    Row (
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        tokens.forEachIndexed{ index, token ->
            OutlinedTextField(
                value = token,
                onValueChange =  {
                    if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                        onTokenChange(index, it)
                        if (it.isNotEmpty() && index < 5) {
                            focusRequester[index + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester[index])
                    .height(60.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = Constants().centuryFontFamily,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                ),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0x101B1B1B),
                    unfocusedBorderColor = Color(0x101B1B1B),
                    focusedContainerColor = Color(0x201B1B1B),
                    focusedBorderColor = Color(0x101B1B1B)
                )
            )
        }
    }
}
