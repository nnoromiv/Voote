package com.example.voote.view.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DriveEta
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.Routes
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton

@Composable
fun DriverLicenceScreen(navController: NavController) {
    var licenceNumber by remember { mutableStateOf("") }

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp),
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
                DLForm(
                    licenceNumber = licenceNumber,
                    onIdNumberChange = { licenceNumber = it }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                DLButtons(
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun DLForm(
    licenceNumber: String,
    onIdNumberChange: (String) -> Unit
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {

        Component().TextField(
            value = licenceNumber,
            onValueChange = onIdNumberChange,
            label = { Text(
               "Driver Licence Number",
            ) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DriveEta,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
        )
    }
}

@Composable
fun DLButtons(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Continue",
            onClick = {
                navController.navigate(Routes.ADDRESS)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )

        COutlinedButton(
            text = "Do this later",
            onClick = {},
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}