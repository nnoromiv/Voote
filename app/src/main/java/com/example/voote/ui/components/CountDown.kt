package com.example.voote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class CountDown(
    val text: String = "",
    private val totalTime: Int = 10,
    private val onFinished: () -> Unit = {},
    private val contractAddress : String? = null
) {

    @Composable
    fun Card() {
        val height = if (contractAddress != null) 120 else 100

        Card (
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFF1B1B1B))
                    .padding(12.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .fillMaxHeight(),

                    ) {
                    Column {
                        Text(
                            text = text,
                            fontSize = 18,
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        )

                        Timer(
                            totalTime = totalTime,
                            onFinished = onFinished
                        )
                    }
                }

                if (contractAddress != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = contractAddress,
                                fontSize = 10,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                            )

                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy Wallet",
                                tint = Color.White,
                                modifier = Modifier.padding(start = 15.dp).size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Timer(
        totalTime: Int,
        onFinished: () -> Unit = {}
    ){
        var timeLeft by remember { mutableIntStateOf(totalTime) }

        LaunchedEffect(key1 = timeLeft) {
            if (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            } else {
                onFinished()
            }
        }

        Text(
            text = formatTime(timeLeft),
            modifier = Modifier
                .padding(vertical = 15.dp),
            color = Color.White,
            fontSize = 20,
            fontWeight = FontWeight.Bold
        )
    }

    private fun formatTime(seconds: Int): String {
        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return "%02d Days :%02d Hrs :%02d Min :%02d s".format(days, hours, minutes, remainingSeconds)
    }

}