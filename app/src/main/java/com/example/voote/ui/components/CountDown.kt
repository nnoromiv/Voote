package com.example.voote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.utils.helpers.calculateTimeLeftMillis
import com.example.voote.utils.helpers.copyToClipboard
import kotlinx.coroutines.delay


/**
 * CountDown Component
 * @param text Text to display
 * @param walletBalance Wallet Balance
 * @param endTime End Time
 * @param contractAddress Contract Address
 * @param onFinished Callback when countdown finishes
 * @return CountDown Component
 * @author Nnorom Christian
 */

@Composable
fun CountDown(text: String = "", walletBalance: String = "", endTime: Long = 0L, contractAddress : String? = null, onFinished: () -> Unit = {}) {
    val context = LocalContext.current

    val totalTime = calculateTimeLeftMillis(endTime)

    var timeLeft by remember { mutableLongStateOf(totalTime) }

    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else {
            onFinished()
        }
    }

    fun formatTime(seconds: Long): String {
        if (seconds <= 0) {
            return "00 Days :00 Hrs :00 Min :00 s"
        }

        val days = seconds / (24 * 3600)
        val hours = (seconds % (24 * 3600)) / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return "%02d Days :%02d Hrs :%02d Min :%02d s".format(days, hours, minutes, remainingSeconds)
    }

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF1B1B1B))
                .padding(12.dp)
                .fillMaxWidth()
                .fillMaxHeight(),

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (text.length >= 15) text.substring(0, 15) + "..." else text,
                    fontSize = 18,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )

                Text(
                    text = walletBalance.ifEmpty() { formatTime(timeLeft) },
                    modifier = Modifier.padding(top = 15.dp),
                    color = Color.White,
                    fontSize = 20,
                    fontWeight = FontWeight.Bold
                )
            }

            if (contractAddress != null) {
                Row(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = contractAddress,
                        fontSize = 10,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                    )

                    IconButton(
                        onClick = { copyToClipboard(context, "Wallet Address", contractAddress) },
                        modifier = Modifier
                            .padding(start = 5.dp)
                                .size(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy Wallet",
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}

