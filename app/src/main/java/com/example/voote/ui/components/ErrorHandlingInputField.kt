package com.example.voote.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ErrorHandlingInputField (value: String, onValueChange: (String) -> Unit, isError: Boolean, errorMessage: String, label: String, imageVector: ImageVector, keyboardOptions: KeyboardOptions = KeyboardOptions.Default) {

    Column {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(
                text = label,
                fontSize = 16,
                color = if (isError) Color.Red else Color.Black
            ) },
            trailingIcon = {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            keyboardOptions = keyboardOptions,
            singleLine = true,
            isError = isError,
        )

        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

