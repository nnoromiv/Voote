package com.example.voote.view.kyc.driverlicence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo

@Composable
fun DriverLicenceScreen(navController: NavController) {
    var licenceNumber by remember { mutableStateOf("") }

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Component().Text(
                text = "Driver Licence",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )
            Component().Text(
                text = "Verification",
                fontSize = 30,
                fontWeight = FontWeight.Bold,
            )
            Component().Text(
                text = "Provide your Driver Licence Number",
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
                DriverLicenceForm(
                    licenceNumber = licenceNumber,
                    onIdNumberChange = { licenceNumber = it }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                DriverLicenceButtons(
                    navController = navController
                )
            }
        }
    }
}
