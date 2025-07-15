package com.example.voote.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.voote.model.data.ElectionData

@Composable
fun ElectionDropdownMenu(
    elections: List<ElectionData>,
    selectedElectionId: Int?,
    onElectionSelected: (ElectionData) -> Unit,
    label: String = "Select Election"
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedElection = elections.find { it.electionId == selectedElectionId }

    Box(
        modifier = Modifier.fillMaxWidth(),
        ) {
        Column(
            modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = !expanded })
        ) {
            TextField(
                value = selectedElection?.title ?: "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth(),
                readOnly = true,
                enabled = false,
                label = { Text(label) },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentSize()
            ) {
                elections.forEach { election ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                election.title,
                                fontSize = 12,
                                color = Color.Black
                            )
                        },
                        onClick = {
                            onElectionSelected(election)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
