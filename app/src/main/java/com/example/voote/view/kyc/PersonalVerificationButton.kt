package com.example.voote.view.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.RoutePassportVerification
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text

@Composable
fun PersonalVerificationButton(navController: NavController) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Begin Verification",
            onClick = {
                navController.navigate(RoutePassportVerification)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "We have a ",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Privacy Policy",
                onClick = {},
            )
        }
    }
}
