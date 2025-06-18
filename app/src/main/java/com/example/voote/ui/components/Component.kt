package com.example.voote.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextFieldColors
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

@Composable
fun Text(
    text: String,
    fontSize: Int = 16,
    fontWeight: FontWeight? = FontWeight.Normal,
    color: Color = Color.Black,
    softWrap: Boolean = false,
    modifier : Modifier = Modifier,
    textDecoration: TextDecoration? = null,
    style: TextStyle = TextStyle.Default
) {
    androidx.compose.material3.Text(
        text = text,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        fontFamily = Constants().centuryFontFamily,
        color = color,
        modifier = modifier,
        softWrap = softWrap,
        textDecoration = textDecoration,
        style = style
    )
}

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    isError: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontFamily = Constants().centuryFontFamily,
        fontWeight = FontWeight.Normal,
    ),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        focusedPlaceholderColor = Color.Black,
        unfocusedPlaceholderColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        errorIndicatorColor = Color.Red,
        errorLabelColor = Color.Red,
        errorCursorColor = Color.Red,
        errorTrailingIconColor = Color.Red,
        errorLeadingIconColor = Color.Red,
    ),
) {
    androidx.compose.material3.TextField(
        value =  value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        textStyle = textStyle,
        readOnly = readOnly,
        singleLine = singleLine,
        isError = isError,
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        colors = colors,
    )
}