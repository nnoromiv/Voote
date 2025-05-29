package com.example.voote.view.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.Routes
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton

@Composable
fun PersonalVerificationScreen(navController: NavController) {

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Component().Text(
                text = "Personal Information",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )
            Component().Text(
                text = "Verification",
                fontSize = 30,
                fontWeight = FontWeight.Bold,
            )
            Component().Text(
                text = "A valid government issued ID is required to verify your account",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
                softWrap = true,
            )

            HorizontalDivider(
                color = Color(0x401B1B1B),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Top()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Bottom(
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun Top() {
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Component().Text(
            text = "Accepted Detail for verification includes",
            fontSize = 24,
            fontWeight = FontWeight.Bold,
            softWrap = true
        )

        Component().Text(
            text = "1. Passport Number or BRP Number",
            fontSize = 18,
            fontWeight = FontWeight.Normal,
            softWrap = true,
        )

        Component().Text(
            text = "2. Driver's Licence Number",
            fontSize = 18,
            fontWeight = FontWeight.Normal,
        )

        Component().Text(
            text = "3. Residential Address",
            fontSize = 18,
            fontWeight = FontWeight.Normal,
        )

        Component().Text(
            text = "4. Facial Information",
            fontSize = 18,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
fun Bottom(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Continue",
            onClick = {
                navController.navigate(Routes.IDENTITY_NUMBER)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )

        COutlinedButton(
            text = "Skip",
            onClick = {},
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Component().Text(
                text = "We have a ",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Privacy Policy",
                onClick = {},
            )
            Component().Text(
                text = "for this process",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}