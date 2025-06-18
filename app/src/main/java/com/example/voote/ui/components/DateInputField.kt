package com.example.voote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputField(onDateSelected: (Long) -> Unit, label: String, date: String ) {

    val showDialog = remember { mutableStateOf(false) }
    val state = rememberDatePickerState()

    if(showDialog.value) {
        DatePickerDialog (
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                Row(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            PrimaryButton(
                                text = "Cancel",
                                onClick = { showDialog.value = false },
                                colors = ButtonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Red,
                                    disabledContentColor = Color.White
                                )
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            PrimaryButton(
                                text = "Done",
                                onClick = {
                                    if(state.selectedDateMillis != null) {
                                        onDateSelected(state.selectedDateMillis!!)
                                    }

                                    showDialog.value = false
                                }
                            )
                        }
                    }
                }
            },
        ) {
            DatePicker(state)
        }
    }

    Row {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = date,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(label) },
                singleLine = true,
                readOnly = true,
            )
        }
        IconButton(
            onClick = { showDialog.value = true }
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }

    }

}
