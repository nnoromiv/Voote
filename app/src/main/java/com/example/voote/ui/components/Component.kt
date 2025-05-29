package com.example.voote.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.voote.utils.Constants

class Component {

    @Composable
    fun Text(
        text: String,
        fontSize: Int,
        fontWeight: FontWeight? = FontWeight.Normal,
        color: Color = Color.Black,
        softWrap: Boolean = false,
        @SuppressLint("ModifierParameter") modifier : Modifier = Modifier,
        textDecoration: TextDecoration? = null
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            fontFamily = Constants().centuryFontFamily,
            color = color,
            modifier = modifier,
            softWrap = softWrap,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun TextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: @Composable (() -> Unit)? = null,
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        visualTransformation: VisualTransformation = VisualTransformation.None,
        trailingIcon: @Composable (() -> Unit)? = null
    ) {
        TextField(
            value =  value,
            onValueChange = onValueChange,
            label = label,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontFamily = Constants().centuryFontFamily,
                fontWeight = FontWeight.Normal,
            ),
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = Color.Black,
                unfocusedPlaceholderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
            )
        )
    }
}