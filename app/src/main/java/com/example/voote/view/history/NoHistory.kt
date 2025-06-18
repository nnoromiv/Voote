package com.example.voote.view.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.Text

@Composable
fun NoHistory() {
    Scaffold {
            innerPadding ->
        Column (
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Icon(
                Icons.Outlined.History,
                contentDescription = "History",
                modifier = Modifier.size(100.dp),
                tint = Color.Gray
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                "You have not cast any vote.",
                fontSize = 18
            )

        }
    }
}