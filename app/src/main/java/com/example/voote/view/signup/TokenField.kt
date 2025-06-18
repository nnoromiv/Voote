package com.example.voote.view.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voote.utils.Constants

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
                    .fillMaxWidth()
                    .height(50.dp),
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