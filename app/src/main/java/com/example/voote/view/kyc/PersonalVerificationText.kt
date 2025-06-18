package com.example.voote.view.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.Text

@Composable
fun PersonalVerificationText() {
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Text(
            text = "Accepted Detail for verification includes",
            fontSize = 24,
            fontWeight = FontWeight.Bold,
            softWrap = true
        )

        Text(
            text = "1. Passport Number or BRP Number",
            fontSize = 18,
            fontWeight = FontWeight.Normal,
            softWrap = true,
        )

        Text(
            text = "2. Driver's Licence Number",
            fontSize = 18,
            fontWeight = FontWeight.Normal,
        )

        Text(
            text = "3. Residential Address",
            fontSize = 18,
            fontWeight = FontWeight.Normal,
        )

        Text(
            text = "4. Facial Information",
            fontSize = 18,
            fontWeight = FontWeight.Normal,
        )
    }
}
