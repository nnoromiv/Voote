package com.example.voote.view.election

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.Text

@Composable
fun CountCard() {
    Card(
        modifier = Modifier
            .wrapContentWidth()
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF1B1B1B))
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            Column {
                Text(
                    text = "Total Vote Count",
                    fontSize = 14,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "13 974 799",
                    fontSize = 24,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}