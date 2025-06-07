package com.example.voote.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.outlinedButtonBorder
import androidx.compose.material3.ButtonDefaults.textShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voote.utils.Constants

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(5.dp),
    colors: ButtonColors? = null,
    border: BorderStroke? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        colors = colors
            ?: ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B1B1B),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color(0xFF1B1B1B)
            ),
        shape = shape,
        border = border
    ) {
        Text(
            text,
            fontSize = 18.sp,
            fontFamily = Constants().centuryFontFamily
        )
    }
}

@Composable
fun COutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(5.dp),
    colors: ButtonColors? = null,
    border: BorderStroke? = outlinedButtonBorder(enabled)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = enabled,
        colors = colors
            ?: ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF1B1B1B),
            disabledContentColor = Color.White
            ),
        shape = shape,
        border = border
    ) {
        Text(
            text,
            fontSize = 18.sp,
            fontFamily = Constants().centuryFontFamily
        )
    }
}

@Composable
fun CTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Color.Black,
    fonSize: Int = 18,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.wrapContentSize(),
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
        shape = textShape
    ) {
        Text(
            text,
            fontSize = fonSize.sp,
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = Constants().centuryFontFamily,
            style = TextStyle(
                textDecoration = TextDecoration.Underline,
            ),
            textAlign = TextAlign.Right,
        )
    }
}

